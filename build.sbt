import sbtrelease.ReleaseStateTransformations._

organization := "com.gu"
name := "dynamo-db-switches"
scalaVersion := "2.12.8"
val awsSdkVersion = "2.16.25"

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "dynamodb" % awsSdkVersion,
  "software.amazon.awssdk" % "dynamodb-enhanced" % awsSdkVersion,
  "org.clapper" %% "grizzled-slf4j" % "1.3.0",
  "org.scalacheck" %% "scalacheck" % "1.12.6" % "test"
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

