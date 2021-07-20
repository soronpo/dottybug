opaque type DFBoolOrBit <: DFType.Of[ir.DFBoolOrBit] =
  DFType.Of[ir.DFBoolOrBit]

object DFBoolOrBit:
  object Ops:
    extension (dfVal: DFVal[DFBoolOrBit]) def asBool: Unit = dfVal.as(DFBool)
  end Ops
end DFBoolOrBit

import DFBoolOrBit.Ops.*

final val DFBool = ir.DFBool.asInstanceOf[DFBoolOrBit]
