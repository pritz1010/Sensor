name := "sensor"

version := "0.1"

scalaVersion := "2.13.3"

val AkkaVersion = "2.6.8"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.typelevel" %% "cats-effect" % "2.1.4",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0",
  "com.github.tototoshi" %% "scala-csv" % "1.3.6",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "org.scalatest" %% "scalatest" % "3.2.0" % Test
)

scalacOptions := Seq(
  "-deprecation"
)