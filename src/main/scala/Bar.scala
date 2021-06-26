import scala.quoted.*
import compiletime.ops.any.==
trait Check22[
  Wide1,
  Wide2,
  Cond[T1 <: Wide1, T2 <: Wide2]
]:
  type Check[T1 <: Wide1, T2 <: Wide2] =
    Check22.Check[Cond[T1, T2]]

object Check22:
  trait Check[
    CondValue 
  ]
  inline given [
    CondValue 
  ]: Check[CondValue] =
    ${ checkMacro[CondValue] }

  final def checkMacro[
    CondValue 
  ](using
    Quotes,
    Type[CondValue]
  ): Expr[Check[CondValue]] =
    import quotes.reflect.*
    val condValue = TypeRepr.of[CondValue]
    println(condValue)
    '{
    new Check[CondValue] {}
    }
  end checkMacro
end Check22

object Samurai
  extends Check22[
    Int,
    Int,
    [W <: Int, VW <: Int] =>> W == VW,
  ]

trait DFType2
opaque type DFBits2[W <: Int] <: DFType2 = DFType2
object DFBits2:
  trait TC[-T <: DFType2, V]
  object TC:
    given DFBitsTokenFromToken[W <: Int, VW <: Int](using
      check: Samurai.Check[W, VW]
    ): TC[DFBits2[W], DFBits2[VW]] with {}