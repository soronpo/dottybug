// Minimized repro for the lowmelvin/hammer-scala COMPILER failure on
// 3.9.0-RC1-bin-20260501-0c8c581-NIGHTLY (Scala 3 community-build3, 2026-05-01).
//
// Failing job:
//   https://github.com/VirtusLab/community-build3/actions/runs/25237182777/job/74005935133
// Original test that triggered the failure:
//   src/test/scala/com/melvinlow/hammer/HammerSpec.scala:27
//
// Reproduce:
//   scalac Repro.scala
//
// Compiler error:
//   No given instance of type Hammer[D, given_HasT.T] was found.
//   I found:
//       Lib.derived[D, B](given_HasT)
//   But given instance derived in object Lib does not match
//   type Hammer[D, given_HasT.T].
//
// Note: dealiased, `given_HasT.T` is `B`, so the candidate
// `Lib.derived[D, B]` *does* have type `Hammer[D, B]` = `Hammer[D, given_HasT.T]`.
// The compiler nonetheless rejects it.
//
// Adjacent prior bugs (closed but with a similar shape — "found but does
// not match"):
//   * scala/scala3#25417 / #25427 — fixed in PR #25448 for opaque types.
//   * scala/scala3#22585 — earlier hammer-scala regression on 3.6.4.

import scala.compiletime.summonInline

trait Hammer[I, O]
trait HasT { type T }

object Lib:
  inline given derived[I, O](using h: HasT): Hammer[I, O] =
    val _ = summonInline[Hammer[I, h.T]]
    new Hammer[I, O] {}

import Lib.given

class B
class D
given HasT with { type T = B }

@main def m(): Unit =
  val h: Hammer[D, D] = summon[Hammer[D, D]]
