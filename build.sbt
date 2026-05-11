ThisBuild / scalaVersion := "3.7.4"

lazy val lib = (project in file("lib"))
  .settings(name := "cb-hammer-i26018-lib")

lazy val test = (project in file("test"))
  .dependsOn(lib)
  .settings(name := "cb-hammer-i26018-test")

lazy val root = (project in file("."))
  .aggregate(lib, test)
  .settings(name := "cb-hammer-i26018")
