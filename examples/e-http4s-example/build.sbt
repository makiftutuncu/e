val logbackVersion = "1.2.3"
val eVersion       = "1.1.2"
val http4sVersion  = "0.21.1"
val flywayVersion  = "6.1.4"
val doobieVersion  = "0.8.8"
val catsVersion    = "2.1.0"

lazy val root = (project in file("."))
  .settings(
    organization := "dev.akif",
    name := "e-http4s-example",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      "ch.qos.logback"  % "logback-classic"     % logbackVersion,
      "dev.akif"       %% "e-scala"             % eVersion,
      "dev.akif"       %% "e-circe"             % eVersion,
      "org.http4s"     %% "http4s-blaze-server" % http4sVersion,
      "org.http4s"     %% "http4s-circe"        % http4sVersion,
      "org.http4s"     %% "http4s-dsl"          % http4sVersion,
      "org.flywaydb"    % "flyway-core"         % flywayVersion,
      "org.tpolecat"   %% "doobie-core"         % doobieVersion,
      "org.tpolecat"   %% "doobie-h2"           % doobieVersion,
      "org.typelevel"  %% "cats-core"           % catsVersion,
      "org.typelevel"  %% "cats-effect"         % catsVersion
    )
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings"
)
