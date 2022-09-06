val scala3Version = "3.2.1-RC1-bin-SNAPSHOT"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-simple",
    version := "0.1.0",

    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-unchecked",
      "-feature",
      "-language:implicitConversions",
      "-deprecation",
      "-Ydebug-unpickling"
    )


  )
