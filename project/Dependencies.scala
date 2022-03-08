import sbt._

object Dependencies {
  lazy val catsCore       = "org.typelevel"        %% "cats-core"    % "2.3.1"
  lazy val circeCore      = "io.circe"             %% "circe-core"   % "0.14.1"
  lazy val circeParser    = "io.circe"             %% "circe-parser" % "0.14.1"
  lazy val gson           = "com.google.code.gson"  % "gson"         % "2.8.6"
  lazy val playJson       = "com.typesafe.play"    %% "play-json"    % "2.9.2"
  lazy val zio            = "dev.zio"              %% "zio"          % "1.0.5"

  lazy val jUnit           = "org.junit.jupiter"  % "junit-jupiter"     % "5.7.2" % Test
  lazy val jUnitInterface  = "net.aichler"        % "jupiter-interface" % "0.8.4" % Test
  lazy val mUnit           = "org.scalameta"     %% "munit"             % "0.7.29" % Test
  lazy val mUnitScalaCheck = "org.scalameta"     %% "munit-scalacheck"  % "0.7.29" % Test
  lazy val zioTest         = "dev.zio"           %% "zio-test"          % "1.0.5" % Test
  lazy val zioTestSBT      = "dev.zio"           %% "zio-test-sbt"      % "1.0.5" % Test
}
