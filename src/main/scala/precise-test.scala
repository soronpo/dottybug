import preciseLib.*

import scala.annotation.precise

trait TC[@precise -T]:
  type Type <: DFTypeAny
object TC:
  transparent inline given ofDFType[T <: DFTypeAny]: TC[T] = new TC[T]:
    type Type = T

extension [T, @precise D <: Int](t: T)(using tc: TC[T])
  def XX(
    cellDim: D
  ): DFVector[tc.Type, Tuple1[D]] = ???

object check:
  val x: DFVector[DFVector[DFBool, Tuple1[4]], Tuple1[4]] = DFBool XX 4 XX 4