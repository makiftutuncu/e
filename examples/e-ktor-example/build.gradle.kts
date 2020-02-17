import org.gradle.api.tasks.testing.logging.TestLogEvent.*

val kotlinVersion   = "1.3.61"
val ktorVersion     = "1.2.6"
val exposedVersion  = "0.20.3"
val h2Version       = "1.4.200"
val hikaricpVersion = "3.4.2"
val flywayVersion   = "6.1.4"
val logbackVersion  = "1.2.1"
val eVersion        = "1.1.1"

plugins {
    application
    kotlin("jvm") version "1.3.61"
}

group = "dev.akif"
version = "0.0.1-SNAPSHOT"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    // Ktor
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    // Database
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("com.zaxxer:HikariCP:$hikaricpVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // e
    implementation("dev.akif:e-kotlin:$eVersion")
    implementation("dev.akif:e-gson:$eVersion")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")

sourceSets["main"].resources.srcDirs("resources")
