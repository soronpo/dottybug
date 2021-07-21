opaque type DFType = Int
object DFType:
  extension (dfType: Int) def asFE[T <: DFType]: T = dfType.asInstanceOf[T]

  trait TC[T]:
    type Type <: DFType
    def apply(t: T): Type
  object TC:
    given ofDFType[T <: DFType]: TC[T] with
      type Type = T
      def apply(t: T): Type = t
  end TC

export DFType.asFE
