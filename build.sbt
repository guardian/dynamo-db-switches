import Dependencies._

organization := "com.gu"

name := "dynamodbswitches"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  liftJson,
  amazonWebServicesSdk,
  grizzled
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
