package preciseLib.core
import scala.annotation.unchecked.uncheckedVariance
import scala.annotation.precise

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

trait TC[@precise -T]:
  type Type <: DFTypeAny
  def apply(t: T): Type
object TC:
  transparent inline given ofDFType[T <: DFTypeAny]: TC[T] = new TC[T]:
    type Type = T
    def apply(t: T): Type = t
  transparent inline given ofBooleanCompanion: TC[Boolean.type] = new TC[Boolean.type] :
    type Type = DFBool
    def apply(t: Boolean.type): Type = ???

object Ops:
  extension [T, @precise D <: Int](t: T)(using tc: TC[T])
    def XX(
      cellDim: D
    ): DFVector[tc.Type, Tuple1[D]] = ???
