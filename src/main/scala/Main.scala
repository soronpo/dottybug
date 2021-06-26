import scala.quoted.*
trait Comp[A, B]
trait Catch22:
  type Check[T1 <: Int, T2 <: Int] =
    Catch22.Check[Comp[T1, T2]]

object Catch22:
  trait Check[CondValue]
  given [CondValue]: Check[CondValue] with {}
end Catch22

object Samurai extends Catch22

trait Never
opaque type Lie[W <: Int] <: Never = Never
object Lie:
  trait TC[-T <: Never, V] :
    type Out
  object TC:
    given [W <: Int, VW <: Int](using
      check: Samurai.Check[W, VW]
    ): TC[Lie[W], Lie[VW]] with
      type Out = Samurai.Check[W, VW]

val x  = summon[Lie.TC[Lie[7], Lie[9]]]
val works = summon[x.Out =:= Samurai.Check[7, 9]]
