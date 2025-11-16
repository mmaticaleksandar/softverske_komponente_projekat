package report.txt

import report.api.*
import report.calculations.DefaultCalculationEngine

class TxtReportProvider: ReportProvider {

    private val engine = DefaultCalculationEngine()

    override val id = "txt"
    override val displayName = "Plain Text Report"
    override val supportedFormats = setOf(ReportFormat.PLAIN_TEXT)

    override fun createGenerator(format: ReportFormat): ReportGenerator {
        require(format == ReportFormat.PLAIN_TEXT)
        return TxtReportGenerator(engine)
    }
}