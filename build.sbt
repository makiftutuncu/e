// === Dependency Versions ===

val javaVersion = "21"
val latestKotlinVersion = "2.0.0"
val latestScalaVersion = "3.4.2"

val catsCore = "org.typelevel" %% "cats-core" % "2.10.0"
val circeCore = "io.circe" %% "circe-core" % "0.14.7"
val circeParser = "io.circe" %% "circe-parser" % "0.14.7"
val gson = "com.google.code.gson" % "gson" % "2.11.0"
val playJson = "org.playframework" %% "play-json" % "3.0.4"
val zio = "dev.zio" %% "zio" % "2.1.1"

val jUnit = "org.junit.jupiter" % "junit-jupiter" % "5.10.2" % Test
val jUnitInterface = "net.aichler" % "jupiter-interface" % "0.11.1" % Test
val mUnit = "org.scalameta" %% "munit" % "1.0.0" % Test
val mUnitScalaCheck = "org.scalameta" %% "munit-scalacheck" % "1.0.0" % Test
val zioTest = "dev.zio" %% "zio-test" % "2.1.1" % Test
val zioTestSBT = "dev.zio" %% "zio-test-sbt" % "2.1.1" % Test

// === Settings ===

import java.time.Year

Global / onChangedBuildSource := ReloadOnSourceChanges

val commonSettings = Seq(
  resolvers += Resolver.jcenterRepo
)

val mdocSettings = Seq(
  (publish / skip) := true,
  mdocVariables := Map(
    "VERSION" -> version.value,
    "JAVA_VERSION" -> javaVersion,
    "SCALA_VERSION" -> latestScalaVersion,
    "KOTLIN_VERSION" -> latestKotlinVersion,
    "COPYRIGHT_YEAR" -> Year.now.toString
  ),
  mdocIn := file("docs"),
  mdocOut := file(".")
)

val javaSettings: Seq[Setting[?]] = commonSettings ++ Seq(
  crossPaths := false, // Do not append Scala versions to the generated artifacts
  autoScalaLibrary := false, // Exclude Scala related libraries

  javacOptions ++= Seq("-source", javaVersion),
  libraryDependencies ++= Seq(
    jUnit,
    jUnitInterface
  ),
  (Test / classLoaderLayeringStrategy) := ClassLoaderLayeringStrategy.Flat,
  testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
)

val scalaSettings: Seq[Setting[?]] = {
    commonSettings ++ Seq(
      scalaVersion := latestScalaVersion,
      javacOptions ++= Seq("-source", javaVersion),
      autoAPIMappings := true,
      exportJars := true,
      apiMappings ++= {
          val classpath = (Compile / fullClasspath).value

          def findJar(name: String): File = {
              val regex = ("/" + name + "[^/]*.jar$").r
              classpath.find { jar => regex.findFirstIn(jar.data.toString).nonEmpty }.get.data
          }

          Map(
            findJar("scala3-library") -> url(s"https://scala-lang.org/api/$latestScalaVersion/")
          )
      },
      testFrameworks += new TestFramework("munit.Framework"),
      libraryDependencies ++= Seq(
        mUnit,
        mUnitScalaCheck
      )
    )
}

lazy val kotlinSettings: Seq[Setting[?]] = javaSettings ++ Seq(
  kotlinLib("stdlib"),
  kotlinVersion := latestKotlinVersion,

  // Delegate doc generation to Gradle and Dokka
  (Compile / doc) := {
      import sys.process.*
      Process(Seq("./gradlew", "dokkaJavadoc"), baseDirectory.value).!
      target.value / "api"
  },

  // Include Kotlin files in sources
  (Compile / packageConfiguration) := {
      val old = (Compile / packageSrc / packageConfiguration).value
      val newSources = (Compile / sourceDirectories).value.flatMap(f => (f ** "*.kt").get)

      new Package.Configuration(
        old.sources ++ newSources.map(f => f -> f.getName),
        old.jar,
        old.options
      )
  },
  libraryDependencies ++= Seq(
    jUnit,
    jUnitInterface
  ),
  testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
)

// === Modules ===

lazy val e = project
    .in(file("."))
    .aggregate(`e-scala`, `e-kotlin`, `e-java`, `e-circe`, `e-play-json`, `e-gson`, `e-zio`)

lazy val `e-docs` = project
    .in(file("e-docs"))
    .dependsOn(`e-scala`, `e-kotlin`, `e-java`, `e-circe`, `e-play-json`, `e-gson`, `e-zio`)
    .enablePlugins(MdocPlugin)
    .settings(scalaSettings)
    .settings(mdocSettings)

lazy val `e-scala` = project
    .in(file("e-scala"))
    .settings(scalaSettings)

lazy val `e-kotlin` = project
    .in(file("e-kotlin"))
    .settings(kotlinSettings)
    .enablePlugins(KotlinPlugin)

lazy val `e-java` = project
    .in(file("e-java"))
    .settings(javaSettings)

lazy val `e-circe` = project
    .in(file("e-circe"))
    .dependsOn(`e-scala` % "compile->compile;test->test")
    .settings(scalaSettings)
    .settings(
      libraryDependencies ++= Seq(
        circeCore,
        circeParser
      )
    )

lazy val `e-play-json` = project
    .in(file("e-play-json"))
    .dependsOn(`e-scala` % "compile->compile;test->test")
    .settings(scalaSettings)
    .settings(
      libraryDependencies ++= Seq(
        catsCore,
        playJson
      )
    )

lazy val `e-zio` = project
    .in(file("e-zio"))
    .dependsOn(`e-scala` % "compile->compile;test->test")
    .settings(scalaSettings)
    .settings(
      libraryDependencies ++= Seq(
        zio,
        zioTest,
        zioTestSBT
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )

lazy val `e-gson` = project
    .in(file("e-gson"))
    .dependsOn(`e-java` % "compile->compile;test->test")
    .settings(javaSettings)
    .settings(
      libraryDependencies ++= Seq(
        gson
      )
    )

// === Project Metadata ===

ThisBuild / description := "A zero-dependency micro library to deal with errors"
ThisBuild / homepage := Some(url("https://github.com/makiftutuncu/e"))
ThisBuild / startYear := Some(2019)
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / organization := "dev.akif"
ThisBuild / organizationName := "Mehmet Akif Tütüncü"
ThisBuild / organizationHomepage := Some(url("https://akif.dev"))
ThisBuild / developers := List(
  Developer("makiftutuncu", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev"))
)
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/makiftutuncu/e"), "git@github.com:makiftutuncu/e.git"))
ThisBuild / versionScheme := Some("early-semver")

// === Release Settings ===

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations.*

val sonatypeUser = sys.env.getOrElse("SONATYPE_USER", "")
val sonatypePass = sys.env.getOrElse("SONATYPE_PASS", "")

ThisBuild / credentials ++= Seq(
  Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    sonatypeUser,
    sonatypePass
  ),
  Credentials(
    "GnuPG Key ID",
    "gpg",
    "3D5A9AE9F71508A0D85E78DF877A4F41752BB3B5",
    "ignored"
  )
)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := {
    Some(
      if (isSnapshot.value) "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
      else "releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
}

Compile / packageBin / publishArtifact := true
Compile / packageSrc / publishArtifact := true
Compile / packageDoc / publishArtifact := true

val checkPublishCredentials = ReleaseStep { state =>
    if (sonatypeUser.isEmpty || sonatypePass.isEmpty) {
        throw new Exception(
          "Sonatype credentials are missing! Make sure to provide SONATYPE_USER and SONATYPE_PASS environment variables."
        )
    }

    state
}

val runMDoc = ReleaseStep(
  releaseStepCommand("e-docs/mdoc") andThen { st =>
      import sys.process.*
      Process(Seq("git", "add", "-A"), st.baseDir).!
      st
  }
)

usePgpKeyHex("3D5A9AE9F71508A0D85E78DF877A4F41752BB3B5")

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  checkPublishCredentials,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  runMDoc,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("e-scala/publishSigned"),
  releaseStepCommandAndRemaining("e-java/publishSigned"),
  releaseStepCommandAndRemaining("e-kotlin/publishSigned"),
  releaseStepCommandAndRemaining("e-circe/publishSigned"),
  releaseStepCommandAndRemaining("e-play-json/publishSigned"),
  releaseStepCommandAndRemaining("e-gson/publishSigned"),
  releaseStepCommandAndRemaining("e-zio/publishSigned"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
