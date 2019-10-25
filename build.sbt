ThisBuild / description          := "A zero-dependency micro library to model errors"
ThisBuild / homepage             := Some(url("https://github.com/makiftutuncu/e"))
ThisBuild / startYear            := Some(2019)
ThisBuild / licenses             := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / organizationName     := "Mehmet Akif Tütüncü"
ThisBuild / organization         := "dev.akif"
ThisBuild / organizationHomepage := Some(url("https://akif.dev"))
ThisBuild / developers           := List(Developer("1", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev")))
ThisBuild / apiURL               := Some(url("https://github.com/makiftutuncu/e/blob/API.md"))
ThisBuild / scmInfo              := Some(ScmInfo(url("https://github.com/makiftutuncu/e"), "scm:git:git@github.com:makiftutuncu/e.git"))

ThisBuild / scalaVersion       := "2.13.1"
ThisBuild / crossScalaVersions := Seq("2.12.10", scalaVersion.value)

ThisBuild / publishMavenStyle := true
ThisBuild / publishTo         := Some("GitHub makiftutuncu Apache Maven Packages" at "https://maven.pkg.github.com/makiftutuncu/e")
ThisBuild / credentials       += Credentials("GitHub Package Registry", "maven.pkg.github.com", "makiftutuncu", sys.env.getOrElse("GITHUB_TOKEN", "N/A"))

lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % Test

lazy val e = project
  .in(file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(`e-core`)

lazy val `e-core` = project
  .in(file("core"))
  .settings(
    libraryDependencies ++= Seq(
      scalaTest
    )
  )
