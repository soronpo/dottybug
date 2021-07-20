import compiler.ir

opaque type DFType[T <: ir.DFType] = T
type DFBoolOrBit = DFType[ir.DFBoolOrBit]

object DFBoolOrBit:
  def asBool(dfType: DFBoolOrBit): Unit = {}

export DFBoolOrBit.*