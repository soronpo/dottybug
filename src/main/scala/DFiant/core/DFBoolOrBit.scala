package DFiant.core
import DFiant.compiler.ir
opaque type DFBoolOrBit <: DFType.Of[ir.DFBoolOrBit] =
  DFType.Of[ir.DFBoolOrBit]

object DFBoolOrBit:
  object Ops:
    extension (dfVal: DFVal[DFBoolOrBit]) def asBool: Unit = {}
  end Ops
end DFBoolOrBit

export DFBoolOrBit.Ops.*

