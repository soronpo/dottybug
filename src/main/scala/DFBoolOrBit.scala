import compiler.ir
opaque type DFBoolOrBit <: DFType.Of[ir.DFBoolOrBit] =
  DFType.Of[ir.DFBoolOrBit]

object DFBoolOrBit:
  def asBool(dfVal: DFVal[DFBoolOrBit]): Unit = {}

export DFBoolOrBit.*