import sbt._

object Dependencies {
  lazy val circeCore      = "io.circe"             %% "circe-core"        % "0.13.0"
  lazy val circeParser    = "io.circe"             %% "circe-parser"      % "0.13.0"
  lazy val gson           = "com.google.code.gson"  % "gson"              % "2.8.6"
  lazy val playJson       = "com.typesafe.play"    %% "play-json"         % "2.8.1"
  lazy val zio            = "dev.zio"              %% "zio"               % "1.0.0-RC18-1"
  lazy val jUnit          = "org.junit.jupiter"     % "junit-jupiter"     % "5.6.0" % Test
  lazy val jUnitInterface = "net.aichler"           % "jupiter-interface" % "0.8.3" % Test
  lazy val scalaTest      = "org.scalatest"        %% "scalatest"         % "3.1.1" % Test
}
