package report.cli

import report.api.*
import java.nio.file.Path
import java.nio.file.Files

class CsvDataSource(
    private val path: Path,
    private val hasHeader: Boolean = true
) {

    fun load(): TableData {
        //Citamo sve linije fajla
        val lines = Files.readAllLines(path).filter { it.isNotBlank() }


        //Proveravamo da li je gajl prazan, ako jesete vracamo poruku
        require(lines.isNotEmpty()) { "CSV file is empty" }

        //Ako ima header, smestamo ga u header, aa ako nema brojimo koliko ima kolona i pravimo imena kolona
        val header = if (hasHeader) {
            lines.first().split(",")
        } else {
            val cols = lines.first().split(",").size
            (1..cols).map { "col$it" }
        }
        //Ako ima header preskacemo jedan reed
        val entries = if (hasHeader) lines.drop(1) else lines


        //Pravimo nazive za kolone
        val cols = header.map {
            ColumnDefinition(
                name = it.trim(),
                title = it.trim(),
                type = ColumnType.STRING
            )
        }


        val rows = entries.map { line ->
            //Delimo red po zarezu
            val parts = line.split(",")


            val map = mutableMapOf<String, Any?>()

            //Ide po svim kolonama, koristi se ime kolone kao kljuc i smesta se vrednost
            header.forEachIndexed { idx, col ->
                map[col.trim()] = parts.getOrNull(idx)?.trim()
            }
            //Smesta u jedan row podatke iz mape
            Row(map)
        }
        //Vraca tabelu sa podacima
        return TableData(cols, rows)
    }
}
