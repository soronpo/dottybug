package mylib
import scala.quoted.*

object Main:
  extension (inline sc: StringContext)
    transparent inline def foo(inline args: Any*): Any = 1

object ImportWorks:
  extension (inline sc: StringContext)
    transparent inline def bar(inline args: Any*): Any = 1

export Main.*