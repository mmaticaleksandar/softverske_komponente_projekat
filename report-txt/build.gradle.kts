plugins {
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":report-api"))
    implementation(project(":report-calculations"))
}


kotlin {
    jvmToolchain(21)
}