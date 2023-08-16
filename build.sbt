name := "event-hub-example-app"

version := "0.1"

scalaVersion := "2.12.11"
val AkkaVersion = "2.6.12"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.7",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.lihaoyi" %% "upickle" % "0.9.5"
)
