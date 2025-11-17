plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}



repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":report-api"))
    implementation(project(":report-calculations"))
    implementation(project(":report-txt"))
    implementation(project(":report-htmlpdf"))
    implementation(project(":report-md"))
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.3")

}


kotlin {
    jvmToolchain(21)
}


application {
    mainClass.set("report.cli.MainKt")
}