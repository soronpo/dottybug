import compiler.ir
opaque type DFBoolOrBit <: DFType.Of[ir.DFBoolOrBit] =
  DFType.Of[ir.DFBoolOrBit]

object DFBoolOrBit:
  def asBool(dfType: DFBoolOrBit): Unit = {}

export DFBoolOrBit.*