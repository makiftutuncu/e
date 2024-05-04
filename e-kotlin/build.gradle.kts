import java.net.URI

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "dev.akif"

repositories {
    mavenCentral()
}

layout.buildDirectory = file("target")

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.10.2")
}

tasks.dokkaJavadoc.configure {
    outputDirectory.set(layout.buildDirectory.dir("target/api")) // SBT will look into here while doing `packageDoc`
    dokkaSourceSets {
        named("main") {
            configureEach {
                apiVersion.set(version.toString())
                includeNonPublic.set(true)
                reportUndocumented.set(true)
                failOnWarning.set(false)
                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                    remoteUrl.set(
                        URI.create("https://github.com/makiftutuncu/e/blob/master/e-kotlin/src/main/kotlin").toURL()
                    )
                    remoteLineSuffix.set("#L")
                }
                jdkVersion.set(8)
                externalDocumentationLink {
                    url.set(URI.create("https://javadoc.io/doc/dev.akif/e-kotlin/").toURL())
                    packageListUrl.set(URI.create("https://javadoc.io/doc/dev.akif/e-kotlin/").toURL())
                }
            }
        }
    }
}
