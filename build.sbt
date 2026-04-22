ThisBuild / scalaVersion := "3.8.3"
ThisBuild / version      := "0.1.0"

// Reproduces https://github.com/scala/scala3/issues/20010
//
// The crash is triggered when the compiler, while completing a user class
// that extends a library class, has to look up a parent of the library
// class whose type has been moved/removed from the classpath.
//
//   libA    : defines `parent.ParsingTest`
//   libB    : defines `child.ValidatingTest extends parent.ParsingTest`
//             (the class file references `parent.ParsingTest` as a parent)
//   root    : extends `child.ValidatingTest`, but sees only libB — not libA
//
// With libA missing from root's classpath, loading `ValidatingTest`'s parent
// produces a TypeRef whose prefix is a TermRef chain of synthetic package
// objects, which hits the `non-class parent` assertion in SymDenotations.

lazy val libA = (project in file("libA"))
  .settings(name := "lib-a")

lazy val libB = (project in file("libB"))
  .dependsOn(libA)
  .settings(name := "lib-b")

lazy val root = (project in file("."))
  .dependsOn(libB)
  .settings(
    name := "repro-20010",
    // Hide libA's outputs from root's classpath. libB still compiles
    // against libA at its own compile time, so its class/tasty files carry
    // a reference to parent.ParsingTest that can no longer be resolved
    // while typechecking root/src.
    Compile / internalDependencyClasspath := {
      val cp       = (Compile / internalDependencyClasspath).value
      val libAOut  = (libA / Compile / classDirectory).value
      cp.filterNot(_.data == libAOut)
    }
  )
