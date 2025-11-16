package report.api
//Interfejs koji ce implementirati svaka biblioteka
//Generator jednog formata
interface ReportGenerator {
    val format: ReportFormat           // koji format generiše
    val fileExtension: String          // npr "txt", "html", "pdf", "md"

    /**
     * Generiše sadržaj izveštaja.
     * Vraćamo ByteArray da bi moglo direktno da se upiše u fajl.
     */
    fun generate(document: ReportDocument): ByteArray
}
// Provider – SPI entry point
interface ReportProvider {
    val id: String                     // tehnički ID, npr "txt"
    val displayName: String            // za korisnički prikaz, npr "Plain text TXT"
    val supportedFormats: Set<ReportFormat>

    fun createGenerator(format: ReportFormat): ReportGenerator
}
