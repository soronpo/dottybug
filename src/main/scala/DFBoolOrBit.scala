opaque type DFBoolOrBit <: DFType with HasToken =
  DFType with HasToken

trait HasToken

extension (dfVal: DFVal[DFBoolOrBit]) def asBool: Unit = dfVal.as(DFBool)

final val DFBool: DFBoolOrBit = ???
