package report.txt

import report.api.*
import report.calculations.DefaultCalculationEngine

class TxtReportGenerator(
    private val calc: DefaultCalculationEngine
) : ReportGenerator {

    override val format = ReportFormat.PLAIN_TEXT
    override val fileExtension = "txt"

    override fun generate(document: ReportDocument): ByteArray {

        val sb = StringBuilder()

        document.sections.forEachIndexed { i, section ->
            render(section, sb)
            if (i < document.sections.lastIndex) sb.appendLine().appendLine()
        }

        return sb.toString().toByteArray()
    }

    private fun render(section: ReportSection, sb: StringBuilder) {

        sb.appendLine(section.title)
        sb.appendLine("=".repeat(section.title.length))
        sb.appendLine()

        val table = section.table
        val rows = table.rows.toMutableList()

        computeColumns(table, rows)

        val columns = table.columns
        val widths = columns.map { col ->
            maxOf(
                col.title.length,
                rows.maxOfOrNull { it.values[col.name]?.toString()?.length ?: 0 } ?: 0
            )
        }


//Prikazivanje broja redova
        if (section.showRowNumbers) {
            sb.append(pad("No", 3, CellAlign.RIGHT)).append("  ")
        }

//Prikazivanje headera
        columns.forEachIndexed { idx, col ->
            sb.append(pad(col.title, widths[idx], col.align))
            if (idx < columns.lastIndex) sb.append("  ")
        }
        sb.appendLine()

//Razmak izmedju headera i ostatka tabele
        val underline = sb.lines().last().length
        sb.appendLine("-".repeat(underline))


//Ispis pojedinacnih redova
        rows.forEachIndexed { rowIndex, row ->
            if (section.showRowNumbers) {
                sb.append(pad((rowIndex + 1).toString(), 3, CellAlign.RIGHT)).append("  ")
            }
            columns.forEachIndexed { idx, col ->
                sb.append(pad(row.values[col.name]?.toString() ?: "", widths[idx], col.align))
                if (idx < columns.lastIndex) sb.append("  ")
            }
            sb.appendLine()
        }


// Ispis sume
        if (section.summaryItems.isNotEmpty()) {
            sb.appendLine()
            section.summaryItems.forEach { item ->
                val value = when (val v = item.value) {
                    is SummaryValue.Literal -> v.value
                    is SummaryValue.Aggregation ->
                        calc.aggregate(v.function, v.columnName, rows, v.condition)?.toString() ?: "n/a"
                }
                sb.appendLine("${item.label}: $value")
            }
        }
    }


//Prolazi kroz tabelu i gleda da li ima polja koja treba da se izracunaju
    private fun computeColumns(table: TableData, rows: MutableList<Row>) {
        table.columns.forEach { col ->
            col.computed?.let { expr ->
                rows.forEach { row ->
                    row.values[col.name] = calc.evaluate(expr, row)
                }
            }
        }
    }


//Funkcija za poravnanje teksta
    private fun pad(text: String, width: Int, align: CellAlign): String {
        if (text.length >= width) return text
        val diff = width - text.length
        return when (align) {
            CellAlign.LEFT -> text + " ".repeat(diff)
            CellAlign.RIGHT -> " ".repeat(diff) + text
            CellAlign.CENTER -> {
                val l = diff / 2
                " ".repeat(l) + text + " ".repeat(diff - l)
            }
        }
    }
}
