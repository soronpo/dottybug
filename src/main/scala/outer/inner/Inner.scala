package outer
package inner

sealed trait Foo
object Foo:
  trait TC[T]

  given ofFoo[T <: Foo]: TC[T] = ???

  trait Bar extends Foo

import Foo.TC
val badSummon = summon[TC[Bar]]
