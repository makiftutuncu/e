import java.net.URL

plugins {
    kotlin("jvm") version "1.4.0"
    id("org.jetbrains.dokka") version "1.4.0-rc"
}

group = "dev.akif"

repositories {
    mavenCentral()
    jcenter()
}

buildDir = file("target")

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.6.2")
}

tasks.dokkaJavadoc.configure {
    outputDirectory = "./target/api" // SBT will look into here while doing `packageDoc`
    dokkaSourceSets {
        configureEach {
            apiVersion = version.toString()
            includeNonPublic = true
            reportUndocumented = true
            failOnWarning = false
            sourceLink {
                path = "src/main/kotlin"
                url = "https://github.com/makiftutuncu/e/blob/master/e-kotlin/src/main/kotlin"
                lineSuffix = "#L"
            }
            jdkVersion = 8
            externalDocumentationLink {
                url = URL("https://javadoc.io/doc/dev.akif/e-kotlin/")
                packageListUrl = URL("https://javadoc.io/doc/dev.akif/e-kotlin/")
            }
        }
    }
}
