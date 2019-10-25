// === Dependencies ===

lazy val circeCore   = "io.circe"      %% "circe-core"   % "0.12.3"
lazy val circeParser = "io.circe"      %% "circe-parser" % "0.12.3"
lazy val scalaTest   = "org.scalatest" %% "scalatest"    % "3.0.8" % Test

// === Settings ===

description          in ThisBuild := "A zero-dependency micro library to model errors"
homepage             in ThisBuild := Some(url("https://github.com/makiftutuncu/e"))
startYear            in ThisBuild := Some(2019)
licenses             in ThisBuild := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
organizationName     in ThisBuild := "Mehmet Akif Tütüncü"
organization         in ThisBuild := "dev.akif"
organizationHomepage in ThisBuild := Some(url("https://akif.dev"))
developers           in ThisBuild := List(Developer("1", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev")))
scmInfo              in ThisBuild := Some(ScmInfo(url("https://github.com/makiftutuncu/e"), "git@github.com:makiftutuncu/e.git"))
publishMavenStyle    in ThisBuild := true
publishTo            in ThisBuild := Some("GitHub makiftutuncu Apache Maven Packages" at "https://maven.pkg.github.com/makiftutuncu/e")
credentials          in ThisBuild += Credentials("GitHub Package Registry", "maven.pkg.github.com", "makiftutuncu", sys.env.getOrElse("GITHUB_TOKEN", "N/A"))

lazy val javaSettings = Seq(
  // Do not append Scala versions to the generated artifacts
  crossPaths := false,
  // This forbids including Scala related libraries into the dependency
  autoScalaLibrary := false
)

lazy val scalaSettings = Seq(
  scalaVersion       := "2.13.1",
  crossScalaVersions := Seq("2.12.10", scalaVersion.value),
  libraryDependencies ++= Seq(
    scalaTest
  )
)

// === Modules ===

lazy val e = project
  .in(file("."))
  .aggregate(`e-core`, `e-scala`, `e-circe`)
  .settings(
    skip in publish := true
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
