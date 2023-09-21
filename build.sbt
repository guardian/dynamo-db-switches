organization := "com.gu"
name := "dynamo-db-switches"
scalaVersion := "2.13.10"
crossScalaVersions := Seq(scalaVersion.value, "2.12.17")

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "dynamodb" % "2.18.24",
  "org.clapper" %% "grizzled-slf4j" % "1.3.4",
  "org.scalacheck" %% "scalacheck" % "1.17.0" % Test
)

Compile / doc / sources := List()
