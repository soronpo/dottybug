package dfhdl

object core:

  import scala.annotation.precise

  object DFType:
    object Ops:
      trait Floozy[V]

      extension[T] (t: T) def tester[@precise V](tokenValue: V): Floozy[V] = ???

object hdl:
  export core.DFType.Ops.*

end hdl

export hdl.*
