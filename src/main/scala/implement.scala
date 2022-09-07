import scala.annotation.precise
import scala.annotation.unchecked.uncheckedVariance

object preciseLib:
  sealed trait Args
  sealed trait NoArgs extends Args
  sealed trait Args2[T1, T2] extends Args
  trait IRDFType
  trait IRDFVector extends IRDFType
  trait IRDFBool extends IRDFType
  sealed trait DFError
  class DFType[+T <: IRDFType, +A <: Args]
  type DFTypeAny = DFType[IRDFType, Args]
  object DFBool extends DFType[IRDFBool, NoArgs]
  type DFBool = DFBool.type 
  type DFVector[+T <: DFTypeAny, +D <: NonEmptyTuple] =
    DFType[IRDFVector, Args2[T @uncheckedVariance, D @uncheckedVariance]]

