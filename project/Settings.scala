import kotlin.Keys.{kotlinLib, kotlinVersion}
import mdoc.MdocPlugin.autoImport.{mdocIn, mdocOut, mdocVariables}
import net.aichler.jupiter.sbt.Import.jupiterTestFramework
import sbt.Keys._
import sbt._

object Settings {
  lazy val javaVersion         = "1.8"
  lazy val latestKotlinVersion = "1.5.32"
  lazy val latestScalaVersion  = "2.13.8"

  lazy val crossCompiledScalaVersions = Seq("2.12.16", latestScalaVersion)

  lazy val commonSettings = Seq(
    resolvers += Resolver.jcenterRepo
  )

  lazy val mdocSettings = Seq(
    (publish / skip) := true,
    mdocVariables := Map(
      "VERSION"              -> version.value,
      "JAVA_VERSION"         -> javaVersion,
      "SCALA_VERSION"        -> latestScalaVersion,
      "KOTLIN_VERSION"       -> latestKotlinVersion,
      "CROSS_SCALA_VERSIONS" -> crossCompiledScalaVersions.mkString(", "),
      "COPYRIGHT_YEAR"       -> "2020"
    ),
    mdocIn := file("docs"),
    mdocOut := file(".")
  )

  lazy val javaSettings: Seq[Setting[_]] = commonSettings ++ Seq(
    crossPaths := false,       // Do not append Scala versions to the generated artifacts
    autoScalaLibrary := false, // Exclude Scala related libraries

    javacOptions ++= Seq("-source", javaVersion),

    libraryDependencies ++= Seq(
      Dependencies.jUnit,
      Dependencies.jUnitInterface
    ),

    (Test / classLoaderLayeringStrategy) := ClassLoaderLayeringStrategy.Flat,

    testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
  )

  lazy val scalaSettings: Seq[Setting[_]] = {
    commonSettings ++ Seq(
      scalaVersion       := latestScalaVersion,
      crossScalaVersions := crossCompiledScalaVersions,

      (Compile / unmanagedSourceDirectories) ++= (
        if (scalaVersion.value.startsWith("2.12")) {
          Seq((Compile / sourceDirectory).value / s"scala-${scalaBinaryVersion.value}")
        } else {
          Seq.empty
        }
      ),

      javacOptions ++= Seq("-source", javaVersion),

      scalacOptions ++= Seq(
        "-encoding", "utf8"
        , "-feature"
        , "-deprecation"
        , "-language:implicitConversions"
        , "-language:higherKinds"
        , "-Xfatal-warnings"
        , "-Xlint:unused"
        //, "-Vimplicits"
      ),

      scalacOptions ++= (
        if (scalaVersion.value.startsWith("2.12")) {
          Seq.empty
        } else {
          Seq("-Xlint:implicit-not-found")
        }
      ),

      autoAPIMappings := true,
      exportJars := true,
      apiMappings ++= {
        val classpath = (Compile / fullClasspath).value
        def findJar(name: String): File = {
          val regex = ("/" + name + "[^/]*.jar$").r
          classpath.find { jar => regex.findFirstIn(jar.data.toString).nonEmpty }.get.data
        }

        Map(
          findJar("scala-library") -> url(s"http://scala-lang.org/api/${scalaVersion.value}/")
        )
      },

      testFrameworks += new TestFramework("munit.Framework"),

      libraryDependencies ++= Seq(
        Dependencies.mUnit,
        Dependencies.mUnitScalaCheck
      )
    )
  }

  lazy val kotlinSettings: Seq[Setting[_]] = javaSettings ++ Seq(
    kotlinLib("stdlib"),

    kotlinVersion := latestKotlinVersion,

    // Delegate doc generation to Gradle and Dokka
    (Compile / doc) := {
      import sys.process._
      Process(Seq("./gradlew", "dokkaJavadoc"), baseDirectory.value).!
      target.value / "api"
    },

    // Include Kotlin files in sources
    (Compile / packageConfiguration) := {
      val old = (Compile / packageSrc / packageConfiguration).value
      val newSources = (Compile / sourceDirectories).value.flatMap(_ ** "*.kt" get)

      new Package.Configuration(
        old.sources ++ newSources.map(f => f -> f.getName),
        old.jar,
        old.options
      )
    },

    libraryDependencies ++= Seq(
      Dependencies.jUnit,
      Dependencies.jUnitInterface
    ),

    testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
  )
}
