package DFiant.core
import compiler.ir
opaque type DFBoolOrBit <: DFType.Of[ir.DFBoolOrBit] =
  DFType.Of[ir.DFBoolOrBit]

object DFBoolOrBit:
  object Ops:
    extension (dfVal: DFVal[DFBoolOrBit]) def asBool: Unit = {}

export DFBoolOrBit.Ops.*

