// Must be compiled separately from Lib.scala — putting the test in the same
// compilation unit as the library makes the bug disappear (the inline given
// expansion only goes wrong when reconstructed from TASTy at use site).
package usage

import mh.{*, given}

final case class A(x: Int)
final case class B(x: Int)
final case class C(a: A)
final case class D(b: B)

object Test {
  val r: Hammer[C, D] = summon
}
