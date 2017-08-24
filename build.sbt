organization := "com.gu"

name := "dynamo-db-switches"

version := "0.4"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.10.28",
  "org.clapper" %% "grizzled-slf4j" % "1.0.2",
  "org.scalacheck" %% "scalacheck" % "1.10.1" % "test"
)

publishTo := {
  if (isSnapshot.value)
    Some(Resolver.file("guardian github snapshots", file(System.getProperty("user.home") + "/guardian.github.com/maven/repo-snapshots")))
  else
    Some(Resolver.file("guardian github releases", file(System.getProperty("user.home") + "/guardian.github.com/maven/repo-releases")))
}