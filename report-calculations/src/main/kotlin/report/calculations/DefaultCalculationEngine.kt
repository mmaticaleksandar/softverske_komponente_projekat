package report.calculations
import report.api.*

class DefaultCalculationEngine :AggregationEngine, RowExpressionEvaluator{
    override fun aggregate(
        function: AggregationFunction,
        columnName: String,
        rows: List<Row>,
        condition: Condition?
    ): Double? {
        //Proveravamo da li su nam podaci filtrirani, ako nisu racunamo sve redova, ako jesu uzimamo samo one koji odgovaraju filteru
        val filtered = if (condition == null) {
            rows
        } else {
            rows.filter { row ->
                row.values[condition.columnName] == condition.expectedValue
            }
        }

        //Uzima elemente iz kolone sa imenom columnName i pokusava da ih pretvori u double i smesti u listu numericValues
        val numericValues = filtered.mapNotNull { row ->
            row.values[columnName]?.toString()?.toDoubleOrNull()
        }


        if (numericValues.isEmpty() && function != AggregationFunction.COUNT && function != AggregationFunction.COUNT_IF) {
            return null
        }

        return when (function) {
            AggregationFunction.SUM ->
                numericValues.sum()

            AggregationFunction.AVERAGE ->
                numericValues.average()

            AggregationFunction.MIN ->
                numericValues.minOrNull()

            AggregationFunction.MAX ->
                numericValues.maxOrNull()

            AggregationFunction.COUNT ->
                filtered.size.toDouble()

            AggregationFunction.COUNT_IF ->
                filtered.count { row ->
                    row.values[columnName] == condition?.expectedValue
                }.toDouble()
        }
    }
    // expression- sta se racuna, row- koji red se racuna
    override fun evaluate(expression: ComputedColumn, row: Row): Double? =
        when (expression) {
            is ComputedColumn.Sum -> {
                val nums = expression.operands.mapNotNull {
                    row.values[it]?.toString()?.toDoubleOrNull()
                }
                nums.sum()
            }

            is ComputedColumn.Difference -> {
                val a = row.values[expression.left]?.toString()?.toDoubleOrNull()
                val b = row.values[expression.right]?.toString()?.toDoubleOrNull()
                if (a != null && b != null) a - b else null
            }

            is ComputedColumn.Product -> {
                val nums = expression.operands.mapNotNull {
                    row.values[it]?.toString()?.toDoubleOrNull()
                }
                nums.fold(1.0) { acc, d -> acc * d }
            }

            is ComputedColumn.Division -> {
                val num = row.values[expression.numerator]?.toString()?.toDoubleOrNull()
                val den = row.values[expression.denominator]?.toString()?.toDoubleOrNull()
                if (num != null && den != null && den != 0.0) num / den else null
            }
        }

}