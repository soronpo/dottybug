package DFiant.core
import DFiant.compiler.ir

opaque type DFType = ir.DFType
object DFType:
  opaque type Of[+T <: ir.DFType] <: DFType = T
  trait TC[T]:
    type Type <: DFType
    def apply(t: T): Type
  object TC:
    given ofDFType[T <: DFType]: TC[T] with
      type Type = T
      def apply(t: T): Type = t
  end TC
