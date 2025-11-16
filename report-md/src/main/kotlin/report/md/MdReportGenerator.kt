package report.md

import report.api.*
import report.calculations.DefaultCalculationEngine

class MdReportGenerator(
    private val calc: DefaultCalculationEngine
) : ReportGenerator {

    override val format = ReportFormat.MARKDOWN
    override val fileExtension = "md"

    override fun generate(document: ReportDocument): ByteArray {
        val sb = StringBuilder()
        //Prolazi se kroz razlicite delove
        document.sections.forEachIndexed { index, sec ->
            //Naslovi tabela
            sb.appendLine("## ${sec.title}")
            sb.appendLine()

            val table = sec.table
            val rows = table.rows.toMutableList()


            //Izracunljiva polja tabele
            table.columns.forEach { col ->
                col.computed?.let { expr ->
                    rows.forEach { r ->
                        r.values[col.name] = calc.evaluate(expr, r)
                    }
                }
            }


            //Header tabele
            val headers = buildList {
                if (sec.showRowNumbers) add("#")
                addAll(table.columns.map { it.title })
            }


            //Ispis headera
            sb.appendLine(headers.joinToString(" | ", prefix = "| ", postfix = " |"))
            sb.appendLine(headers.joinToString(" | ", prefix = "| ", postfix = " |") { "---" })


            //Ispis redova
            rows.forEachIndexed { idx, r ->
                val cells = buildList {
                    if (sec.showRowNumbers) add((idx + 1).toString())
                    addAll(table.columns.map { col -> r.values[col.name]?.toString() ?: "" })
                }
                sb.appendLine(cells.joinToString(" | ", prefix = "| ", postfix = " |"))
            }


            //Sume na kraju
            if (sec.summaryItems.isNotEmpty()) {
                sb.appendLine()
                sec.summaryItems.forEach { item ->
                    val v = when (val sv = item.value) {
                        is SummaryValue.Literal -> sv.value
                        is SummaryValue.Aggregation ->
                            calc.aggregate(sv.function, sv.columnName, rows, sv.condition)?.toString() ?: "n/a"
                    }
                    sb.appendLine("- **${item.label}:** $v")
                }
            }
            //Dodajemo 2 prazne linije izmedju tabela
            if (index < document.sections.lastIndex) sb.appendLine().appendLine()
        }

        return sb.toString().toByteArray()
    }
}
