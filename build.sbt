// === Project Definition ===

description          in ThisBuild := "A zero-dependency micro library for handling errors"
homepage             in ThisBuild := Some(url("https://github.com/makiftutuncu/e"))
startYear            in ThisBuild := Some(2019)
licenses             in ThisBuild := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
organization         in ThisBuild := "dev.akif"
organizationName     in ThisBuild := "Mehmet Akif Tütüncü"
organizationHomepage in ThisBuild := Some(url("https://akif.dev"))
developers           in ThisBuild := List(Developer("makiftutuncu", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev")))
scmInfo              in ThisBuild := Some(ScmInfo(url("https://github.com/makiftutuncu/e"), "git@github.com:makiftutuncu/e.git"))

// === Modules ===

lazy val e = project
  .in(file("."))
  .aggregate(
    `e-core`,
    `e-java`,
    `e-scala`,
    `e-kotlin`,
    `e-circe`,
    `e-play-json`,
    `e-gson`,
    /*
    `e-zio`
    */
  )
  .enablePlugins(MdocPlugin)
  .settings(Settings.mdocSettings)

lazy val `e-core` = project
  .in(file("e-core"))
  .settings(Settings.javaSettings)

lazy val `e-java` = project
  .in(file("e-java"))
  .dependsOn(`e-core`)
  .settings(Settings.javaSettings)

lazy val `e-scala` = project
  .in(file("e-scala"))
  .dependsOn(`e-core`)
  .settings(Settings.scalaSettings)

lazy val `e-kotlin` = project
  .in(file("e-kotlin"))
  .dependsOn(`e-core`)
  .settings(Settings.kotlinSettings)

lazy val `e-circe` = project
  .in(file("e-circe"))
  .dependsOn(`e-scala`)
  .settings(Settings.scalaSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.circeCore,
      Dependencies.circeParser
    )
  )

lazy val `e-play-json` = project
  .in(file("e-play-json"))
  .dependsOn(`e-scala`)
  .settings(Settings.scalaSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.playJson
    )
  )

lazy val `e-gson` = project
  .in(file("e-gson"))
  .dependsOn(`e-java`)
  .settings(Settings.javaSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.gson
    )
  )

/*
lazy val `e-zio` = project
  .in(file("e-zio"))
  .dependsOn(`e-scala`)
  .settings(scalaSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.zio
    )
  )
*/

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
  releaseStepCommandAndRemaining("e/mdoc"),
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("e-core/publishSigned"),
  releaseStepCommandAndRemaining("e-java/publishSigned"),
  releaseStepCommandAndRemaining("+e-scala/publishSigned"),
  releaseStepCommandAndRemaining("e-kotlin/publishSigned"),
  releaseStepCommandAndRemaining("+e-circe/publishSigned"),
  releaseStepCommandAndRemaining("+e-play-json/publishSigned"),
  releaseStepCommandAndRemaining("e-gson/publishSigned"),
  /*
  releaseStepCommandAndRemaining("+e-zio/publishSigned"),
  */
  setNextVersion,
  commitNextVersion,
  pushChanges
)
