
object Foo:
  opaque type BlaBla[+T, D] = Int
  extension [T, D](token: BlaBla[T, D]) def data: D = ???

def foo[W <: Int](value: Bar.BlaBla[W]): Unit = ??? //value.data