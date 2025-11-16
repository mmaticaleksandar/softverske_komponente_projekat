plugins {
    kotlin("jvm")
    application
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
}


kotlin {
    jvmToolchain(21)
}


application {
    mainClass.set("report.cli.MainKt")
}