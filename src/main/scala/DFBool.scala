opaque type DFBool <: DFType with HasToken =
  DFType with HasToken

trait HasToken

extension (dfVal: DFVal[DFBool]) def bar: Unit = dfVal.as(DFBool)

final val DFBool: DFBool = ???
