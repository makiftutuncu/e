name := """e-play-example"""
organization := "dev.akif"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

val eVersion     = "2.0.0"
val anormVersion = "2.6.5"
val h2Version    = "1.4.200"

libraryDependencies ++= Seq(
  evolutions,
  jdbc,
  "dev.akif"                %% "e-scala"     % eVersion,
  "dev.akif"                %% "e-play-json" % eVersion,
  "dev.akif"                %% "e-zio"       % eVersion,
  "org.playframework.anorm" %% "anorm"       % anormVersion,
  "com.h2database"           % "h2"          % h2Version
)
