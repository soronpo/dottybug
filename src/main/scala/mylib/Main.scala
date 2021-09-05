package mylib
import scala.quoted.*

private object Main:
  extension (inline sc: StringContext)
    transparent inline def d(inline args: Any*): Unit =
      ${interpMacro}
  def interpMacro(using Quotes) : Expr[Unit] =
    import quotes.reflect.*
    '{}

export Main.d
