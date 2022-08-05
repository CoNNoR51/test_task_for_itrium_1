ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "wiki_parser",
    libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.2",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2",
    libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.3"
  )
