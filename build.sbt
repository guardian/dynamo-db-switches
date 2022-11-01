import sbtrelease.ReleaseStateTransformations._

organization := "com.gu"
name := "dynamo-db-switches"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "dynamodb" % "2.18.6",
  "org.clapper" %% "grizzled-slf4j" % "1.3.4",
  "org.scalacheck" %% "scalacheck" % "1.17.0" % Test
)

Compile / doc / sources := List()
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)

