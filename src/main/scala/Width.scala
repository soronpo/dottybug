import scala.quoted.*

trait Width
object Width:
  inline given Width = ${ getWidthMacro }
  def getWidthMacro(using Quotes): Expr[Width] = '{ new Width {} }