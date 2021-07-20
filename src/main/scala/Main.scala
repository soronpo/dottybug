trait Comp[A]
trait Catch22:
  type Check[T1 <: Int] = Catch22.Check[Comp[T1]]

object Catch22:
  trait Check[CondValue]
  given [CondValue]: Check[CondValue] with {}
end Catch22

object Samurai extends Catch22

trait Never
opaque type Lie[W <: Int] <: Never = Never
object Lie:
  trait TC[-T <: Never] :
    type Out
  object TC:
    given [W <: Int](using
      check: Samurai.Check[W]
    ): TC[Lie[W]] with
      type Out = Samurai.Check[W]

val x  = summon[Lie.TC[Lie[7]]]
val works = summon[x.Out =:= Samurai.Check[7]]
