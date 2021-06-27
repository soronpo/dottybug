package mylib
import scala.quoted.*

object Export:
  transparent inline def exported: Any = 1

object Import:
  transparent inline def imported: Any = 1

export Export.*