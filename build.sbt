ThisBuild / scalaVersion := "3.9.0-RC1"

// 3.9.0-RC1 is on Maven Central, so no extra resolver is needed. It already
// contains the fix from https://github.com/scala/scala3/pull/25900.
libraryDependencies += "org.scala-lang" %% "scala3-compiler" % scalaVersion.value

// Helper used by repro.sh: dump the resolved compiler classpath to compiler.cp
// so the two-stage reproduction can drive scalac directly via `java`.
lazy val printClasspath = taskKey[Unit]("Write the Scala compiler classpath to compiler.cp")
printClasspath := {
  val cp = (Compile / dependencyClasspath).value.map(_.data.getAbsolutePath)
  IO.write(baseDirectory.value / "compiler.cp", cp.mkString(java.io.File.pathSeparator))
}
