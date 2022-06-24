import sbtrelease.ReleaseStateTransformations._

organization := "com.gu"
name := "dynamo-db-switches"
scalaVersion := "2.11.8"
libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.12.236",
  "org.clapper" %% "grizzled-slf4j" % "1.3.0",
  "org.scalacheck" %% "scalacheck" % "1.12.6" % "test"
)
sources in doc in Compile := List()
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

