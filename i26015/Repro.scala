// Bug: -rewrite -source:3.4-migration converts `with` to `&` in typed
// patterns, but the resulting `case ... : A & B` does not parse.
// The grammar for the typed-pattern ascription accepts `A with B` (compound
// type) but not `A & B` (infix type).
//
// Filed: scala/scala3#26015.
//
// Reproduce:
//   scalac Repro.scala
//     => deprecation warning, "rewritten automatically under
//        -rewrite -source 3.4-migration"
//   scalac -rewrite -source 3.4-migration Repro.scala
//     => rewrites the source to `case N(child: A & B)`
//   scalac Repro.scala  (after rewrite)
//     => [E040] Syntax Error: ',' or ')' expected, but identifier found

trait A
trait B
case class N(child: Any)

def m(n: N): String = n match
  case N(child: A with B) => "ab"
  case _                  => "other"
