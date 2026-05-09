// Regression: `Option.orNull[T]` with an explicit type parameter no
// longer compiles after scala/scala3#25733 (3.9.0-nightly).
//
// Before #25733, the public signature was
//   def orNull[A1 >: A](implicit ev: Null <:< A1): A1
// so call sites of the form `opt.orNull[T]` were valid. The PR
// rewrote the public signature to
//   def orNull: A | Null
// and demoted the type-parameterized overload to `protected` (kept
// only for binary/TASTy compatibility), making it inaccessible from
// user code. As a result, perfectly valid Scala 3.x source no longer
// compiles, and the resulting error message is misleading.
//
// Reproduces with: scalac Repro.scala
//   3.7.0, 3.7.3, 3.8.0, 3.8.1, 3.8.2, 3.8.3 — compiles cleanly.
//   3.9.0-RC1-bin-20260501-0c8c581-NIGHTLY      — fails with E007.
//
// Observed error on 3.9-nightly:
//   -- Error: Repro.scala:NN -----------------------------------------
//   NN |  val a: String = opt.orNull[String]
//      |                  ^^^^^^^^^^^^^^^^^^
//      |             method apply in class StringOps does not take type parameters
//
// (The error message references `StringOps.apply` because the `[String]`
// is reparsed against the inferred result type — but the underlying
// regression is that the call shape `opt.orNull[T]` is no longer valid.)

object Repro:
  val opt: Option[String] = Some("hi")
  val a: String = opt.orNull[String]
