plugins {
    kotlin("jvm") version "2.1.20"
}


allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    // Zajedničke postavke za sve module
    apply(plugin = "org.jetbrains.kotlin.jvm")

    kotlin {
        jvmToolchain(21)
    }

    dependencies {
        // Ako hoćeš svuda logovanje, test libs itd.
        testImplementation(kotlin("test"))
    }
}