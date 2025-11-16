package report.htmlpdf

import report.api.*
import report.calculations.DefaultCalculationEngine

class HtmlPdfReportProvider : ReportProvider {

    private val engine = DefaultCalculationEngine()

    override val id = "htmlpdf"
    override val displayName = "HTML/PDF Report"
    override val supportedFormats = setOf(ReportFormat.HTML, ReportFormat.PDF)

    override fun createGenerator(format: ReportFormat): ReportGenerator =
        HtmlPdfReportGenerator(format, engine)
}