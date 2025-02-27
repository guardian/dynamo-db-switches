import ReleaseTransformations.*
import sbtversionpolicy.withsbtrelease.ReleaseVersion

organization := "com.gu"
name := "dynamo-db-switches"
licenses := Seq(License.Apache2)
scalaVersion := "2.13.14"
scalacOptions ++= Seq("-release:11")
crossScalaVersions := Seq(scalaVersion.value, "2.12.19")

releaseVersion := ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease().value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion
)

// minimum versions of transitive dependencies required to avoid vulnerabilities
val minTransitiveVersions = Seq(
  "io.netty" % "netty-codec-http2" % "4.1.118.Final",
)

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "dynamodb" % "2.20.162",
  "org.clapper" %% "grizzled-slf4j" % "1.3.4",
  "org.scalacheck" %% "scalacheck" % "1.17.0" % Test
) ++ minTransitiveVersions

Compile / doc / sources := List()
