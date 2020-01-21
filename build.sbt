// === Dependencies ===

lazy val circeCore      = "io.circe"             %% "circe-core"        % "0.12.3"
lazy val circeParser    = "io.circe"             %% "circe-parser"      % "0.12.3"
lazy val gson           = "com.google.code.gson"  % "gson"              % "2.8.6"
lazy val playJson       = "com.typesafe.play"    %% "play-json"         % "2.8.1"
lazy val zio            = "dev.zio"              %% "zio"               % "1.0.0-RC17"
lazy val jUnit          = "org.junit.jupiter"     % "junit-jupiter"     % "5.6.0" % Test
lazy val jUnitInterface = "net.aichler"           % "jupiter-interface" % "0.8.3" % Test
lazy val scalaTest      = "org.scalatest"        %% "scalatest"         % "3.1.0" % Test

// === Project Settings ===

description          in ThisBuild := "A zero-dependency micro library to model errors"
homepage             in ThisBuild := Some(url("https://github.com/makiftutuncu/e"))
startYear            in ThisBuild := Some(2019)
licenses             in ThisBuild := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
organization         in ThisBuild := "dev.akif"
organizationName     in ThisBuild := "Mehmet Akif Tütüncü"
organizationHomepage in ThisBuild := Some(url("https://akif.dev"))
developers           in ThisBuild := List(Developer("makiftutuncu", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev")))
scmInfo              in ThisBuild := Some(ScmInfo(url("https://github.com/makiftutuncu/e"), "git@github.com:makiftutuncu/e.git"))

lazy val javaSettings = Seq(
  // Do not append Scala versions to the generated artifacts
  crossPaths := false,
  // This forbids including Scala related libraries into the dependency
  autoScalaLibrary := false,

  resolvers += Resolver.jcenterRepo,

  libraryDependencies ++= Seq(
    jUnit,
    jUnitInterface
  ),

  testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
)

lazy val latestScalaVersion         = "2.13.1"
lazy val crossCompiledScalaVersions = Seq("2.12.10", latestScalaVersion)

lazy val scalaSettings = Seq(
  scalaVersion         := latestScalaVersion,
  crossScalaVersions   := crossCompiledScalaVersions,
  libraryDependencies ++= Seq(
    scalaTest
  )
)

// === Modules ===

lazy val e = project
  .in(file("."))
  .aggregate(`e-core`, `e-scala`, `e-circe`, `e-play-json`, `e-gson`, `e-zio`)
  .enablePlugins(MdocPlugin)
  .settings(
    skip in publish := true,
    mdocVariables := Map(
      "VERSION"              -> version.value,
      "SCALA_VERSION"        -> latestScalaVersion.split("\\.").take(2).mkString("."),
      "CROSS_SCALA_VERSIONS" -> crossCompiledScalaVersions.map(_.split("\\.").take(2).mkString(".")).mkString(", ")
    ),
    mdocOut := file(".")
  )

lazy val `e-core` = project
  .in(file("core"))
  .settings(javaSettings)

lazy val `e-scala` = project
  .in(file("scala"))
  .dependsOn(`e-core`)
  .settings(scalaSettings)

lazy val `e-circe` = project
  .in(file("circe"))
  .dependsOn(`e-scala`)
  .settings(scalaSettings)
  .settings(
    libraryDependencies ++= Seq(
      circeCore,
      circeParser
    )
  )

lazy val `e-play-json` = project
  .in(file("play-json"))
  .dependsOn(`e-scala`)
  .settings(scalaSettings)
  .settings(
    libraryDependencies ++= Seq(
      playJson
    )
  )

lazy val `e-gson` = project
  .in(file("gson"))
  .dependsOn(`e-core`)
  .settings(javaSettings)
  .settings(
    libraryDependencies ++= Seq(
      gson
    )
  )

lazy val `e-zio` = project
  .in(file("zio"))
  .dependsOn(`e-scala`)
  .settings(scalaSettings)
  .settings(
    libraryDependencies ++= Seq(
      zio
    )
  )

// === Release Settings ===

import ReleaseTransformations._

credentials          in ThisBuild += Credentials(Path.userHome / ".sbt" / "sonatype_credential")
pomIncludeRepository in ThisBuild := { _ => false }
publishMavenStyle    in ThisBuild := true
publishTo            in ThisBuild := { Some(if (isSnapshot.value) "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots" else "releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2") }

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("e-core/publishSigned"),
  releaseStepCommandAndRemaining("+e-scala/publishSigned"),
  releaseStepCommandAndRemaining("+e-circe/publishSigned"),
  releaseStepCommandAndRemaining("+e-play-json/publishSigned"),
  releaseStepCommandAndRemaining("e-gson/publishSigned"),
  releaseStepCommandAndRemaining("+e-zio/publishSigned"),
  releaseStepCommandAndRemaining("e/mdoc"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
