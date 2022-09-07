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
  final class DFType[+T <: IRDFType, +A <: Args](val value: T | DFError):
    override def toString: String = value.toString
  type DFTypeAny = DFType[IRDFType, Args]
  type DFBool = DFType[IRDFBool, NoArgs]
  type DFVector[+T <: DFTypeAny, +D <: NonEmptyTuple] =
    DFType[IRDFVector, Args2[T @uncheckedVariance, D @uncheckedVariance]]

