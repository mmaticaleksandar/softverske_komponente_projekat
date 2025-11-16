package report.api

import java.util.ServiceLoader

object ReportProviders {

    fun loadAll(): List<ReportProvider> =
        ServiceLoader.load(ReportProvider::class.java).toList()
        //Trazi sve implementacije ReportProvidera koje su u jaru.Implementacije se ucitavaju u toku izvrsavanja

    fun findById(id: String): ReportProvider? =
        loadAll().find { it.id == id }
    }