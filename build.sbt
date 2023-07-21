val scala3Version = "3.3.2-RC1-bin-20230719-816bd5e-NIGHTLY"

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
