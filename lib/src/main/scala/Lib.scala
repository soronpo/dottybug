// Minimization of lowmelvin/hammer-scala E172 reported as scala/scala3#26018,
// distilled from the project's HammerSpec "should hammer nested fields" test
// (HammerSpec.scala:27).
//
// The previous attempt at #26018 always failed (even on 3.6.4) because it
// dropped the recursive auto-derivation that's the actual trigger. This pair
// compiles cleanly under 3.6.4 but fails under 3.7.4 with the same E172
// "auto.given_X does not match" diagnostic seen in community-build3.
//
// Compile in two passes:
//   scalac -d out Lib.scala
//   scalac -classpath out:<lib> -d out Test.scala
//
// 3.6.4: both pass.
// 3.7.4: Lib.scala compiles (warnings only); Test.scala fails with
//   No given instance of type mh.Hammer[A, B] was found.
//   I found:
//     mh.derived[A, B](A.$asInstanceOf[…], B.$asInstanceOf[…])
//   But given instance derived does not match type mh.Hammer[A, B].
package mh

import scala.compiletime.*
import scala.deriving.*

trait Hammer[I, O] { def hammer(input: I): O }

object Hammer {
  inline def summonFirst[Ts <: Tuple, O]: (Hammer[?, O], Int) =
    inline erasedValue[Ts] match {
      case _: (t *: rest) => summonInline[Hammer[t, O]] -> 0
      case _              => error("not found")
    }

  // Dropping `lazy val (h, idx) = …` (using `val`, or `lazy val tup` +
  // `lazy val h = tup._1`, or eliminating the `Int` slot of the tuple
  // entirely) makes the bug disappear — the destructured-`lazy val` shape is
  // part of the trigger.
  inline def makeOne[S, O](using m: Mirror.ProductOf[S]): Hammer[S, O] =
    new Hammer[S, O] {
      lazy val (h, idx) = summonFirst[m.MirroredElemTypes, O]
      def hammer(source: S): O = h.hammer(source.asInstanceOf)
    }

  inline def makeAll[S: Mirror.ProductOf, Os <: Tuple]: Unit =
    inline erasedValue[Os] match {
      case _: (o *: rest) => makeOne[S, o]; makeAll[S, rest]
      case _: EmptyTuple  => ()
    }

  inline def makeProductHammer[S, O](using
    ms: Mirror.ProductOf[S], mo: Mirror.ProductOf[O]
  ): Hammer[S, O] = {
    makeAll[S, mo.MirroredElemTypes]
    new Hammer[S, O] { def hammer(input: S): O = ??? }
  }
}

given identity[I]: Hammer[I, I] with { def hammer(i: I): I = i }
inline given derived[I: Mirror.ProductOf, O: Mirror.ProductOf]: Hammer[I, O] =
  Hammer.makeProductHammer
