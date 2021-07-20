import compiler.ir

opaque type DFBoolOrBit = ir.DFBoolOrBit

object DFBoolOrBit:
  def asBool(dfType: DFBoolOrBit): Unit = {}

export DFBoolOrBit.*