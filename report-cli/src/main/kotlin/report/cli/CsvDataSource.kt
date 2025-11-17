package report.cli

import report.api.*
import java.nio.file.Files
import java.nio.file.Path

class CsvDataSource(
    private val path: Path,
    private val separator: Char = ','
) : DataSource {

    override fun load(): List<ReportSection> = loadSections()

    fun loadSections(): List<ReportSection> {

        val lines = Files.readAllLines(path)
            .map { it.trim() }

        val sections = mutableListOf<ReportSection>()

        var index = 0
        var sectionNumber = 1

        while (index < lines.size) {

            // preskačemo prazne linije
            while (index < lines.size && lines[index].isBlank()) index++

            if (index >= lines.size) break

            val maybeTitle = lines[index]
            val title: String
            index++

            // ako linija NEMA separator → to je naslov
            // ako linija IMA separator → to je odmah header (nema title)
            if (!maybeTitle.contains(separator)) {
                title = maybeTitle
            } else {
                title = "Section $sectionNumber"
                index--  // vrati se nazad jer je ovo header
            }

            if (index >= lines.size)
                throw IllegalArgumentException("Missing header after section $title")

            val headerLine = lines[index]
            val header = headerLine.split(separator).map { it.trim() }
            index++

            val rows = mutableListOf<Row>()

            while (index < lines.size && lines[index].isNotBlank()) {
                val parts = lines[index].split(separator)

                val map = mutableMapOf<String, Any?>()
                header.forEachIndexed { idx, col ->
                    map[col] = parts.getOrNull(idx)?.trim()
                }

                rows.add(Row(map))
                index++
            }

            val columns = header.map { col ->
                ColumnDefinition(
                    name = col,
                    title = col,
                    type = ColumnType.STRING
                )
            }

            val table = TableData(columns, rows)

            sections.add(
                ReportSection(
                    title = title,
                    showRowNumbers = true,
                    table = table,
                    summaryItems = listOf(
                        SummaryItem(
                            label = "Rows in section",
                            value = SummaryValue.Aggregation(
                                AggregationFunction.COUNT,
                                columns.first().name
                            )
                        )
                    )
                )
            )

            sectionNumber++
            index++
        }

        return sections
    }

}
