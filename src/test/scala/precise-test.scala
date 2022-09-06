import preciseLib.*

class Box[T](x: T)
trait ShowType[T]:
  type Out <: String
object ShowType:
  transparent inline given [T]: ShowType[T] = new ShowType[T]:
    override type Out = "hi"

object check:
//  val v: DFVector[DFBool, Tuple1[4]] = ???
  def box[A](a: A)(using ShowType[A]): Unit = ???
  val b1 = box(Boolean XX 4 XX 4)