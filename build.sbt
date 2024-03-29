organization := "com.gu"
name := "dynamo-db-switches"
scalaVersion := "2.13.10"
crossScalaVersions := Seq(scalaVersion.value, "2.12.17")

// Minimum versions of transitive dependencies required to avoid vulnerabilities
val minTransitiveVersions = Seq(
  "io.netty" % "netty-codec-http2" % "4.1.100.Final",
)

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "dynamodb" % "2.20.162",
  "org.clapper" %% "grizzled-slf4j" % "1.3.4",
  "org.scalacheck" %% "scalacheck" % "1.17.0" % Test
) ++ minTransitiveVersions

Compile / doc / sources := List()
