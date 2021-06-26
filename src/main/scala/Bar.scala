import scala.quoted.*
trait Comp[A, B]
trait Check22:
  type Check[T1 <: Int, T2 <: Int] =
    Check22.Check[Comp[T1, T2]]

object Check22:
  trait Check[CondValue]
  given [CondValue]: Check[CondValue] with {}
end Check22

object Samurai extends Check22

trait DFType2
opaque type DFBits2[W <: Int] <: DFType2 = DFType2
object DFBits2:
  trait TC[-T <: DFType2, V] :
    type Out
  object TC:
    given DFBitsTokenFromToken[W <: Int, VW <: Int](using
      check: Samurai.Check[W, VW]
    ): TC[DFBits2[W], DFBits2[VW]] with
      type Out = Samurai.Check[W, VW]

val x  = summon[DFBits2.TC[DFBits2[7], DFBits2[9]]]
val works = summon[x.Out =:= Samurai.Check[7, 9]]
