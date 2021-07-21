opaque type DFType = Int

object DFType:
  def foo: Unit = ???

  trait TC[T]
  object TC:
    given [T <: DFType]: TC[T] = ???

export DFType.foo

trait DFVal:
  def as[A](using tc: DFType.TC[A], w: Width): Unit = ()