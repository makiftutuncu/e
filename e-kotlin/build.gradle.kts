import java.net.URL

plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.dokka") version "1.6.10"
}

group = "dev.akif"

repositories {
    mavenCentral()
    jcenter()
}

buildDir = file("target")

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.8.2")
}

tasks.dokkaJavadoc.configure {
    outputDirectory.set(buildDir.resolve("target/api")) // SBT will look into here while doing `packageDoc`
    dokkaSourceSets {
        named("main") {
            configureEach {
                apiVersion.set(version.toString())
                includeNonPublic.set(true)
                reportUndocumented.set(true)
                failOnWarning.set(false)
                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                    remoteUrl.set(URL("https://github.com/makiftutuncu/e/blob/master/e-kotlin/src/main/kotlin"))
                    remoteLineSuffix.set("#L")
                }
                jdkVersion.set(8)
                externalDocumentationLink {
                    url.set(URL("https://javadoc.io/doc/dev.akif/e-kotlin/"))
                    packageListUrl.set(URL("https://javadoc.io/doc/dev.akif/e-kotlin/"))
                }
            }
        }
    }
}
