val scala3Version = "3.7.3"

lazy val minilib = project
  .in(file("minilib"))
  .settings(
    name := "minilib",
    scalaVersion := scala3Version,
  )

lazy val root = project
  .in(file("."))
  .dependsOn(minilib)
  .settings(
    name := "scala3-simple",
    version := "0.1.0",
    scalaVersion := scala3Version,
  )
