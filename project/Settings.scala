import kotlin.Keys.{kotlinLib, kotlinVersion}
import mdoc.MdocPlugin.autoImport.{mdocOut, mdocVariables}
import net.aichler.jupiter.sbt.Import.jupiterTestFramework
import sbt.Keys._
import sbt._

object Settings {
  lazy val latestJavaVersion   = "11"
  lazy val latestKotlinVersion = "1.3.70"
  lazy val latestScalaVersion  = "2.13.2"

  lazy val crossCompiledScalaVersions = Seq("2.12.11", latestScalaVersion)

  lazy val commonSettings = Seq(
    resolvers               += Resolver.jcenterRepo,
    Compile / compileOrder  := CompileOrder.JavaThenScala
  )

  lazy val mdocSettings = Seq(
    skip in publish := true,
    mdocVariables := Map(
      "VERSION"              -> version.value,
      "JAVA_VERSION"         -> latestJavaVersion,
      "SCALA_VERSION"        -> latestScalaVersion,
      "KOTLIN_VERSION"       -> latestKotlinVersion,
      "CROSS_SCALA_VERSIONS" -> crossCompiledScalaVersions.mkString(", "),
      "COPYRIGHT_YEAR"       -> "2020"
    ),
    mdocOut := file(".")
  )

  lazy val javaSettings: Seq[Setting[_]] = commonSettings ++ Seq(
    crossPaths := false,       // Do not append Scala versions to the generated artifacts
    autoScalaLibrary := false, // Exclude Scala related libraries

    javacOptions ++= Seq("-source", latestJavaVersion),

    libraryDependencies ++= Seq(
      Dependencies.jUnit,
      Dependencies.jUnitInterface
    ),

    testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
  )

  lazy val scalaSettings: Seq[Setting[_]] = commonSettings ++ Seq(
    scalaVersion       := latestScalaVersion,
    crossScalaVersions := crossCompiledScalaVersions,

    scalacOptions ++= Seq(
      "-encoding", "utf8"
      , "-language:implicitConversions"
      , "-language:higherKinds"
      , "-Xfatal-warnings"
      , "-Xlint:unused"
      , "-Xlint:implicit-not-found"
//      , "-Vimplicits"
    ),

    testFrameworks += new TestFramework("munit.Framework"),

    libraryDependencies ++= Seq(
      Dependencies.mUnit,
      Dependencies.mUnitScalaCheck
    )
  )

  lazy val kotlinSettings: Seq[Setting[_]] = javaSettings ++ Seq(
    kotlinLib("stdlib"),

    kotlinVersion := latestKotlinVersion,

    libraryDependencies ++= Seq(
      Dependencies.jUnit,
      Dependencies.jUnitInterface
    ),

    testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
  )
}
