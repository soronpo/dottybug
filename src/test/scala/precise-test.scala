import preciseLib.*

trait ShowType[T]
object ShowType:
  given [T]: ShowType[T] = ???

object check:
  def box[A](a: A)(using ShowType[A]): Unit = ???
  val b1 = box(Boolean XX 4)