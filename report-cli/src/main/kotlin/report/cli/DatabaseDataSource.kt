package report.cli

import report.api.*
import java.sql.DriverManager

class DatabaseDataSource(
    private val url: String,
    private val user: String,
    private val password: String,
    private val tableName: String
) : DataSource {

    override fun load(): List<ReportSection> {

        val connection = DriverManager.getConnection(url, user, password)
        val statement = connection.createStatement()
        val rs = statement.executeQuery("SELECT * FROM $tableName")

        val meta = rs.metaData
        val colCount = meta.columnCount

        val columns = (1..colCount).map { idx ->
            ColumnDefinition(
                name = meta.getColumnName(idx),
                title = meta.getColumnName(idx),
                type = ColumnType.STRING
            )
        }

        val rows = mutableListOf<Row>()

        while (rs.next()) {
            val map = mutableMapOf<String, Any?>()
            for (i in 1..colCount) {
                map[meta.getColumnName(i)] = rs.getObject(i)?.toString()
            }
            rows.add(Row(map))
        }

        rs.close()
        statement.close()
        connection.close()

        val table = TableData(columns, rows)

        val section = ReportSection(
            title = "Database Section",
            showRowNumbers = true,
            table = table
        )

        return listOf(section)
    }
}
