val scala3Version = "3.0.2-RC1-bin-20210701-9f97b0b-NIGHTLY"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-simple",
    version := "0.1.0",

    scalaVersion := scala3Version,
 )
