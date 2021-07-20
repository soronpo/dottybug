package DFiant.core
import compiler.ir

opaque type DFType = ir.DFType
object DFType:
  opaque type Of[T <: ir.DFType] <: DFType = T

trait DFVal[T <: DFType]
