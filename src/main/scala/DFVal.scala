trait DFType

trait IRToken
extension (token: IRToken) def bits: Unit = ???

trait DFVal
object DFVal:
  export DFBits.Conversions.*
  extension (dfVal: DFVal)
    def width(using Width): Any = ???
    def bits(using Width): Unit = DFBits(dfVal.width)

trait DFToken
extension (token: DFToken) def width(using Width): Any = ???

import scala.quoted.*
trait Width
transparent inline given f: Width = ${ getWidthMacro }
def getWidthMacro(using Quotes): Expr[Width] =
  '{ new Width {} }
