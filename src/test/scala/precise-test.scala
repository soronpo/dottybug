import preciseLib.*

import scala.annotation.precise

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

extension [T, @precise D <: Int](t: T)(using tc: TC[T])
  def XX(
    cellDim: D
  ): DFVector[tc.Type, Tuple1[D]] = ???

trait ShowType[T]
object ShowType:
  given [T]: ShowType[T] = ???

object check:
  def box[A](a: A)(using ShowType[A]): Unit = ???
  val b1 = box(Boolean XX 4)