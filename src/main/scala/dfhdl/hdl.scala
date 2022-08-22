object dfhdl:
  import scala.annotation.precise

  object hdl:
    trait Floozy[V]
    extension[T] (t: T) def tester[@precise V](tokenValue: V): Floozy[V] = ???

  export hdl.*

