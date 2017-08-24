organization := "com.gu"

name := "dynamo-db-switches"

import sbt.Keys.version
import scala.io.Source
import scala.util.Try

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.10.28",
  "org.clapper" %% "grizzled-slf4j" % "1.0.2",
  "org.scalacheck" %% "scalacheck" % "1.10.1" % "test"
)

scmInfo := Some(ScmInfo(
  url("https://github.com/guardian/dynamo-db-switches"),
  "scm:git:git@github.com:guardian/dynamo-db-switches.git"
))

licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

sonatypeProfileName := "com.gu"

pomExtra := (
  <url>https://github.com/guardian/dynamo-db-switches</url>
    <developers>
      <developer>
        <id>Calum-Campbell</id>
        <name>Calum Campbell</name>
        <url>https://github.com/Calum-Campbell</url>
      </developer>
    </developers>
  )


publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

lazy val root = (project in file("."))

pgpSecretRing := file("local.secring.gpg")

pgpPublicRing := file("local.pubring.gpg")

version in ThisBuild := {
  // read from local.version (i.e. teamcity build.number), otherwise use SNAPSHOT
  val version = Try(Source.fromFile("local.version", "UTF-8").mkString.trim).toOption.getOrElse("0.1-SNAPSHOT")
  sLog.value.info(s"using version $version")
  version
}

val release = settingKey[String]("no need")
release in ThisBuild := {
  "There is no need to run `sbt release`, teamcity will automatically have released version 0.<build.counter> when you merged to master"
}