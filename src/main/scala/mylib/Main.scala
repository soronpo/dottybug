package mylib
import scala.quoted.*

private object Main:
  extension (inline sc: StringContext)
    transparent inline def d(inline args: Any*): Unit =
      ${
      interpMacro
      }
  transparent inline def haha() : Unit = {}
  def interpMacro(using Quotes) : Expr[Unit] =
    import quotes.reflect.*
    '{}
