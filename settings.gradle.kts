plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "sK1"
include("report-api")
include("report-calculations")
include("report-txt")
include("report-htmlpdf")
include("report-md")
include("report-cli")