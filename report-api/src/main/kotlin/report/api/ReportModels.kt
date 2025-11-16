package report.api

//Tip kolone
enum class ColumnType {
    STRING, NUMBER, DATE, BOOLEAN
}

//Poravnane u koloni
enum class CellAlign {
    LEFT, RIGHT, CENTER
}

//Izracunljiva polja u tabeli za red
sealed class ComputedColumn {
    data class Sum(val operands: List<String>) : ComputedColumn()
    data class Difference(val left: String, val right: String) : ComputedColumn()
    data class Product(val operands: List<String>) : ComputedColumn()
    data class Division(val numerator: String, val denominator: String) : ComputedColumn()
}

//Definicija kolone
data class ColumnDefinition(
    val name: String,           // intern naziv kolone (id)
    val title: String,          // labela u zaglavlju
    val type: ColumnType,
    val width: Int? = null,
    val align: CellAlign = CellAlign.LEFT,
    val computed: ComputedColumn? = null   // ako nije null, vrednost se računa
)

//Jedan red
data class Row(
    val values: MutableMap<String, Any?>
)

//Tabela
data class TableData(
    val columns: List<ColumnDefinition>,
    val rows: List<Row>
)

// Funkcije za kalkulacije
enum class AggregationFunction {
    SUM, AVERAGE, MIN, MAX, COUNT, COUNT_IF
}

//Uslov za funkcije kalkulacije
data class Condition(
    val columnName: String,
    val expectedValue: Any?)


//Proveravamo da li se suma racuna ili se prima tekst
sealed class SummaryValue {
    data class Literal(val value: String) : SummaryValue()
    data class Aggregation(
        val function: AggregationFunction,
        val columnName: String,
        val condition: Condition? = null
    ) : SummaryValue()
}
//Element sume (ime i vrednost)
data class SummaryItem(
    val label: String,
    val value: SummaryValue
)

//Jedan izvestaj (naslov+tabela+suma)
data class ReportSection(
    val title: String,
    val showRowNumbers: Boolean = false,
    val table: TableData,
    val summaryItems: List<SummaryItem> = emptyList()
)

//Vise izvestaja u jedan dokument
data class ReportDocument(
    val sections: List<ReportSection>
)

// Tip izlaznog formata
enum class ReportFormat {
    PLAIN_TEXT,
    HTML,
    PDF,
    MARKDOWN
}