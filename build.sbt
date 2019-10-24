name := "e"

scalaVersion       := "2.13.1"
crossScalaVersions := Seq("2.11.12", "2.12.10", scalaVersion.value)

description          := "A zero-dependency micro library to model errors"
homepage             := Some(url("https://github.com/makiftutuncu/e"))
startYear            := Some(2019)
licenses             := Seq("MIT" -> url("https://github.com/makiftutuncu/e/blob/LICENSE.md"))
organizationName     := "Mehmet Akif Tütüncü"
organization         := "dev.akif"
organizationHomepage := Some(url("https://akif.dev"))
developers           := List(Developer("1", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev")))
apiURL               := Some(url("https://github.com/makiftutuncu/e/blob/API.md"))
scmInfo              := Some(ScmInfo(url("https://github.com/makiftutuncu/e"), "https://github.com/makiftutuncu/e"))

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % Test

libraryDependencies ++= Seq(
  scalaTest
)
