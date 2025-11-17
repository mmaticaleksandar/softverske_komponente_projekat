package report.cli

import report.api.*
import java.nio.file.Path
import java.nio.file.Files
import java.util.Scanner

fun main(args: Array<String>) {

    if (args.isEmpty()) {
        println("Greska: Morate uneti parametre za fajl ili bazu.")
        println("Primeri:")
        println("  --file C:/path/data.csv")
        println("  --dburl jdbc:mysql://localhost:3306/test --user root --pass 123 --table customers")
        return
    }

    // ================================
    // 1) PARSIRANJE ARGUMENATA
    // ================================

    var filePath: String? = null
    var dbUrl: String? = null
    var dbUser: String? = null
    var dbPass: String? = null
    var dbTable: String? = null

    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--file" -> filePath = args.getOrNull(i + 1)
            "--dburl" -> dbUrl = args.getOrNull(i + 1)
            "--user" -> dbUser = args.getOrNull(i + 1)
            "--pass" -> dbPass = args.getOrNull(i + 1)
            "--table" -> dbTable = args.getOrNull(i + 1)
        }
        i++
    }

    // ================================
    // 2) VALIDACIJA
    // ================================

    val dataSource: DataSource

    if (filePath != null) {
        val path = Path.of(filePath!!)
        if (!Files.exists(path)) {
            println("Greska: fajl ne postoji → $filePath")
            return
        }

        val ext = filePath!!.substringAfterLast(".").lowercase()
        dataSource = when (ext) {
            "csv" -> CsvDataSource(path)
            else  -> error("Nepodrzan tip fajla: $ext")
        }
    }
    else if (dbUrl != null && dbUser != null && dbPass != null && dbTable != null) {
        dataSource = DatabaseDataSource(dbUrl!!, dbUser!!, dbPass!!, dbTable!!)
    }
    else {
        println("Greska: morate navesti ili --file putanju ili --db parametre.")
        return
    }

    // ================================
    // 3) UCITAVANJE PODATAKA
    // ================================
    val sections = dataSource.load()   // CSV/TXT/MD/Baza → vise sectiona

    // ================================
    // 4) IZBOR PROVIDERA
    // ================================
    val providers = ReportProviders.loadAll()
    println("Dostupni provideri:")
    providers.forEachIndexed { idx, p ->
        println("[${idx + 1}] ${p.displayName}")
    }

    val scanner = Scanner(System.`in`)
    var providerChoice: Int? = null
    while (providerChoice !in 1..providers.size) {
        print("Izaberite providera: ")
        providerChoice = scanner.nextLine().toIntOrNull()
    }

    val provider = providers[providerChoice!! - 1]

    // ================================
    // 5) IZBOR FORMATA NA OSNOVU PROVIDERA
    // ================================
    val availableFormats = provider.supportedFormats.toList()

    println("Dostupni formati:")
    availableFormats.forEachIndexed { idx, f ->
        println("[${idx + 1}] $f")
    }

    var formatChoice: Int? = null
    while (formatChoice !in 1..availableFormats.size) {
        print("Izaberite format: ")
        formatChoice = scanner.nextLine().toIntOrNull()
    }

    val format = availableFormats[formatChoice!! - 1]

    // ================================
    // 6) GENERISANJE DOKUMENTA
    // ================================
    val document = ReportDocument(sections)

    val generator = provider.createGenerator(format)
    val outputFileName = "output.${generator.fileExtension}"

    Files.write(Path.of(outputFileName), generator.generate(document))

    println("Izvestaj generisan kao: $outputFileName")
}
