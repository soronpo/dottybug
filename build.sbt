val scala3Version = "3.0.3-RC1-bin-20210716-cc47c56-NIGHTLY"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-simple",
    version := "0.1.0",

    scalaVersion := scala3Version,
 )
