package mylib
import scala.quoted.*

private object Main:
  transparent inline def d(): Unit =
    ${interpMacro}
  def interpMacro(using Quotes) : Expr[Unit] =
    import quotes.reflect.*
    '{}

export Main.d

