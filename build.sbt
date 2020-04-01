scalaVersion := "2.13.1"

name := "reply-guy"
organization := "me.gladwell"
version := "1.0"

libraryDependencies ++= Seq(
  "org.typelevel"          %% "cats-effect"            % "2.0.0",
  "org.twitter4j"          %  "twitter4j-core"         % "4.0.7",
  "co.fs2"                 %% "fs2-core"               % "2.3.0",
  "co.fs2"                 %% "fs2-io"                 % "2.3.0",
  "com.github.pureconfig"  %% "pureconfig-generic"     % "0.12.2",
  "com.github.pureconfig"  %% "pureconfig-cats-effect" % "0.12.2",
  "org.http4s"             %% "http4s-blaze-client"    % "0.21.2",
  "me.gladwell.microtesia" %% "microtesia"             % "0.5.1"
)
