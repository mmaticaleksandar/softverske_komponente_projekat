package report.cli

import report.api.*
import java.nio.file.*

fun main(args: Array<String>) {
    println("Upustvo: <Path> <providerId> <format> <outputPath>")
    //Proverava broj argumenata, potrebana su cetiri
    if (args.size < 4) {
        println("Greska unesite u obliku: <Path> <providerId> <format> <outputPath>")
        return
    }

    val csvPath = Path.of(args[0])
    val providerId = args[1]
    val formatString = args[2]
    val outputPath = Path.of(args[3])


    //Gledamo koji je format prosledjen
    val format =
        when (formatString.lowercase()) {
            "txt" -> ReportFormat.PLAIN_TEXT
            "html" -> ReportFormat.HTML
            "pdf" -> ReportFormat.PDF
            "md" -> ReportFormat.MARKDOWN
            else -> {
                println("Unknown format: $formatString")
                return
            }
        }

    //Trazimo providera na osnovu providerId-a( da li je txt, html, md)
    val providers = ReportProviders.loadAll()
    val provider = providers.find { it.id == providerId }
    if (provider == null) {
        println("Provider '$providerId' not found.")
        return
    }

    //Ako provider ne podrzava format, ispisuje se poruka
    if (!provider.supportedFormats.contains(format)) {
        println("Provider '$providerId' does not support format $format.")
        return
    }


    //Ucitavanje podataka iz CSV-a
    val data = CsvDataSource(csvPath).load()


    //Kreiranje ReportDocumenta
    val document = ReportDocument(
        sections = listOf(
            ReportSection(
                title = "CLI Report", //Naziv dela
                showRowNumbers = true, //Da li se pokazuje redni broj reda
                table = data,
                summaryItems = listOf(  //Lista suma ispod tabele
                    SummaryItem(
                        "Total rows",
                        SummaryValue.Aggregation(AggregationFunction.COUNT, data.columns.first().name)
                    )
                )
            )
        )
    )

    val generator = provider.createGenerator(format)
    val bytes = generator.generate(document)
    Files.write(outputPath, bytes)

    println("Report written to $outputPath")
}
