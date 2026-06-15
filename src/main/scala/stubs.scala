package dfhdl
// This import on the enclosing package is the cycle trigger: resolving it
// forces `DFVal` from TASTy, whose `export DFXInt.Ops.c1` chains back into the
// `c1` given being defined below -> "Cyclic reference involving val <import>".
import DFVal.Ops.CarryOp

trait ExactOp2Aux[Op]

object DFDecimal:
  object Ops:
    export DFXInt.Ops.*

object DFXInt:
  object Ops:
    given c1: ExactOp2Aux[CarryOp] = ???
