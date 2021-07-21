opaque type DFVal[T <: DFType] = Int

extension [T <: DFType](dfVal: DFVal[T])
  def as[A](aliasType: A)(using
      tc: DFType.TC[A],
      w1: Width[?]
  ): DFVal[tc.Type] = ???
