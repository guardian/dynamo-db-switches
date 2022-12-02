import sbtrelease.ReleaseStateTransformations._

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
publishTo := sonatypePublishToBundle.value

releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)