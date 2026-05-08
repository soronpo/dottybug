scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .settings(
    name := "cb-hammer-scala",
    description := "Minimized repro for the lowmelvin/hammer-scala COMPILER failure"
  )
