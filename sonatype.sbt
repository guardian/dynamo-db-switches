sonatypeProfileName := "com.gu"
publishMavenStyle := true
licenses := Seq("ALv2" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
homepage := Some(url("https://github.com/guardian/dynamo-db-switches"))
scmInfo := Some(ScmInfo(url("https://github.com/guardian/dynamo-db-switches"), "git@github.com:guardian/dynamo-db-switches.git"))
developers := List(
  Developer(id="nicl", name="Nic Long", email="", url=url("https://github.com/nicl")),
)
publishTo := sonatypePublishToBundle.value