trait DFVal[+T <: DFType]
extension [T <: DFType](dfVal: DFVal[T])
  def as[A](aliasType: A)(using tc: DFType.TC[A]): DFVal[tc.Type] = ???
