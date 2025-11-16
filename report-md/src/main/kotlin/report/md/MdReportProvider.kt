package report.md

import report.api.*
import report.calculations.DefaultCalculationEngine

class MdReportProvider : ReportProvider {

    private val engine = DefaultCalculationEngine()

    override val id = "md"
    override val displayName = "Markdown Report"
    override val supportedFormats = setOf(ReportFormat.MARKDOWN)

    override fun createGenerator(format: ReportFormat): ReportGenerator {
        require(format == ReportFormat.MARKDOWN)
        return MdReportGenerator(engine)
    }
}