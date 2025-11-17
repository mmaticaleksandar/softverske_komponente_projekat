package report.api

interface DataSource {
    fun load(): List<ReportSection>

}