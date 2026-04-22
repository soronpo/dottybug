package lib

import scala.annotation.StaticAnnotation
import scala.quoted.*

class SqlName(val name: String) extends StaticAnnotation

object Macro:
  transparent inline def tableName[T]: String = ${ tableNameImpl[T] }

  def tableNameImpl[T: Type](using Quotes): Expr[String] =
    import quotes.reflect.*
    val annot = TypeRepr.of[SqlName]
    TypeRepr.of[T].typeSymbol.annotations
      .find(_.tpe =:= annot)
      .map(_.asExprOf[SqlName]) match
      case Some(sqlName) => '{ $sqlName.name }
      case None          => '{ "" }
