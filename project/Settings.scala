import kotlin.Keys.{kotlinLib, kotlinVersion}
import mdoc.MdocPlugin.autoImport.{mdocOut, mdocVariables}
import net.aichler.jupiter.sbt.Import.jupiterTestFramework
import sbt.Keys._
import sbt._

object Settings {
  lazy val javaVersion                = "1.8"
  lazy val latestScalaVersion         = "2.13.1"
  lazy val crossCompiledScalaVersions = Seq("2.12.10", latestScalaVersion)

  lazy val commonSettings = Seq(
    resolvers               += Resolver.jcenterRepo,
    Compile / compileOrder  := CompileOrder.JavaThenScala
  )

  lazy val mdocSettings = Seq(
    skip in publish := true,
    mdocVariables := Map(
      "VERSION"              -> version.value,
      "JAVA_VERSION"         -> javaVersion,
      "SCALA_VERSION"        -> latestScalaVersion.split("\\.").take(2).mkString("."),
      "CROSS_SCALA_VERSIONS" -> crossCompiledScalaVersions.map(_.split("\\.").take(2).mkString(".")).mkString(", "),
      "COPYRIGHT_YEAR"       -> "2020"
    ),
    mdocOut := file(".")
  )

  lazy val javaSettings: Seq[Setting[_]] = commonSettings ++ Seq(
    // Do not append Scala versions to the generated artifacts
    crossPaths := false,
    // This forbids including Scala related libraries into the dependency
    autoScalaLibrary := false,

    libraryDependencies ++= Seq(
      Dependencies.jUnit,
      Dependencies.jUnitInterface
    ),

    testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
  )

  lazy val scalaSettings: Seq[Setting[_]] = commonSettings ++ Seq(
    scalaVersion       := latestScalaVersion,
    crossScalaVersions := crossCompiledScalaVersions,

    libraryDependencies ++= Seq(
      Dependencies.scalaTest
    )
  )

  lazy val kotlinSettings: Seq[Setting[_]] = javaSettings ++ Seq(
    kotlinLib("stdlib"),

    kotlinVersion := "1.3.41"
  )
}
