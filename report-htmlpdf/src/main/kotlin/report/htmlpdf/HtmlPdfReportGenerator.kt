package report.htmlpdf

import report.api.*
import report.calculations.DefaultCalculationEngine

class HtmlPdfReportGenerator(
    private val target: ReportFormat,
    private val calc: DefaultCalculationEngine
) : ReportGenerator {

    //Kazemo da li pravimo html ili pdf fajl
    override val format = target
    override val fileExtension = if (target == ReportFormat.HTML) "html" else "pdf"


    //Glavna funkicja, koja za dati dokument, pretvara u tabelu
    override fun generate(document: ReportDocument): ByteArray {
        val html = buildHtml(document)
        return if (target == ReportFormat.HTML) {
            html.toByteArray()
        } else {
            generatePdefFromHtml(html)
        }
    }


    //Pravljenje pdf fajla
    private fun generatePdefFromHtml(html: String): ByteArray {
        val outputStream = java.io.ByteArrayOutputStream()

        com.openhtmltopdf.pdfboxout.PdfRendererBuilder().apply {
            useFastMode()
            withHtmlContent(html, null)
            toStream(outputStream)
        }.run()

        return outputStream.toByteArray()
    }


    //Pravljenje html dokumenta
    private fun buildHtml(document: ReportDocument): String {
        val sb = StringBuilder()
        sb.appendLine("<html><body>")


        //za svaki deo(section) dokumenta dodajemo zasebni naslov i pravimo tabelu
        document.sections.forEach { sec ->
            sb.appendLine("<h2>${sec.title}</h2>")

            val t = sec.table
            val rows = t.rows.toMutableList()


            //Racunanje computed kolona
            t.columns.forEach { col ->
                col.computed?.let { expr ->
                    rows.forEach { row ->
                        row.values[col.name] = calc.evaluate(expr, row)
                    }
                }
            }


            //Pocetak html tabele
            sb.appendLine("<table border='1' cellspacing='0' cellpadding='4'>")

            //header
            sb.append("<tr>")
            if (sec.showRowNumbers) sb.append("<th>#</th>")
            t.columns.forEach { sb.append("<th>${it.title}</th>") }
            sb.appendLine("</tr>")


            //Redovi tabele
            rows.forEachIndexed { idx, row ->
                sb.append("<tr>")
                if (sec.showRowNumbers) sb.append("<td>${idx + 1}</td>")
                t.columns.forEach { col ->
                    sb.append("<td>${row.values[col.name] ?: ""}</td>")
                }
                sb.appendLine("</tr>")
            }
            sb.appendLine("</table>")



            //Polje sume
            if (sec.summaryItems.isNotEmpty()) {
                sb.appendLine("<ul>")
                sec.summaryItems.forEach { item ->
                    val v = when (val sv = item.value) {
                        is SummaryValue.Literal -> sv.value
                        is SummaryValue.Aggregation ->
                            calc.aggregate(sv.function, sv.columnName, rows, sv.condition)?.toString() ?: "n/a"
                    }
                    sb.appendLine("<li><b>${item.label}:</b> $v</li>")
                }
                sb.appendLine("</ul>")
            }
        }

        sb.appendLine("</body></html>")
        return sb.toString()
    }
}
