import sbt._

object Dependencies {
  lazy val catsCore       = "org.typelevel"        %% "cats-core"    % "2.8.0"
  lazy val circeCore      = "io.circe"             %% "circe-core"   % "0.14.2"
  lazy val circeParser    = "io.circe"             %% "circe-parser" % "0.14.2"
  lazy val gson           = "com.google.code.gson"  % "gson"         % "2.9.0"
  lazy val playJson       = "com.typesafe.play"    %% "play-json"    % "2.9.2"
  lazy val zio            = "dev.zio"              %% "zio"          % "2.0.0"

  lazy val jUnit           = "org.junit.jupiter"  % "junit-jupiter"     % "5.10.2" % Test
  lazy val jUnitInterface  = "net.aichler"        % "jupiter-interface" % "0.10.0" % Test
  lazy val mUnit           = "org.scalameta"     %% "munit"             % "0.7.29" % Test
  lazy val mUnitScalaCheck = "org.scalameta"     %% "munit-scalacheck"  % "0.7.29" % Test
  lazy val zioTest         = "dev.zio"           %% "zio-test"          % "2.0.0" % Test
  lazy val zioTestSBT      = "dev.zio"           %% "zio-test-sbt"      % "2.0.0" % Test
}
