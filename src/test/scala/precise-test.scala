import preciseLib.*

class Box[T](x: T)
trait ShowType[T]
object ShowType:
  given [T]: ShowType[T] = ???

object check:
//  val v: DFVector[DFBool, Tuple1[4]] = ???
  def box[A](a: A)(using ShowType[A]): Unit = ???
  val b1 = box(Boolean XX 4 XX 4)