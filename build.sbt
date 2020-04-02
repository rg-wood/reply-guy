scalaVersion := "2.13.1"

name := "reply-guy"
organization := "me.gladwell"

libraryDependencies ++= Seq(
  "org.typelevel"          %% "cats-effect"            % "2.0.0",
  "org.twitter4j"          %  "twitter4j-core"         % "4.0.7",
  "co.fs2"                 %% "fs2-core"               % "2.3.0",
  "com.github.pureconfig"  %% "pureconfig-generic"     % "0.12.2",
  "com.github.pureconfig"  %% "pureconfig-cats-effect" % "0.12.2",
  "org.http4s"             %% "http4s-blaze-client"    % "0.21.2",
  "me.gladwell.microtesia" %% "microtesia"             % "0.5.1",
  "io.chrisdavenport"      %% "log4cats-core"          % "1.0.1",
  "io.chrisdavenport"      %% "log4cats-slf4j"         % "1.0.1",
  "org.slf4j"              % "slf4j-simple"            % "1.7.30"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates"
)

enablePlugins(JavaAppPackaging)

enablePlugins(BuildInfoPlugin)

buildInfoPackage := "me.gladwell.twitter.reply"

addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3")

addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
