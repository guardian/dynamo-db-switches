import Dependencies._

organization := "com.gu"

name := "dynamo-db-switches"

version := "0.2"

scalaVersion := "2.10.2"

resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  liftJson,
  amazonWebServicesSdk,
  grizzled,
  scalaCheck
)

publishTo <<= (version) { version: String =>
  val publishType = if (version.endsWith("SNAPSHOT")) "snapshots" else "releases"
  Some(
    Resolver.file(
      "guardian github " + publishType,
      file(System.getProperty("user.home") + "/guardian.github.com/maven/repo-" + publishType)
    )
  )
}
