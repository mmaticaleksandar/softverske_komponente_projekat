package report.calculations

import report.api.*

interface AggregationEngine {
    //Koristimo Aggregation i listu redova da izracunamo funkciju nad jednom kolonom
    fun aggregate(
        function: AggregationFunction,
        columnName: String,
        rows: List<Row>,
        condition: Condition? = null
    ): Double?
}

//Racuna vrednost nove kolone na osnovu vrednosti drugih kolona u istom redu
interface RowExpressionEvaluator {
    fun evaluate(expression: ComputedColumn, row: Row): Double?
}