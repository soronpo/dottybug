import compiler.ir
opaque type DFBoolOrBit <: DFType[ir.DFBoolOrBit] = DFType[ir.DFBoolOrBit]

object DFBoolOrBit:
  def asBool(dfType: DFBoolOrBit): Unit = {}

export DFBoolOrBit.*