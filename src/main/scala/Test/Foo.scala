package Test

trait Foo
extension (foo : Foo)
  def fails : Unit = {}
  def works : Unit = {}

extension (bar : Bar)
  def works : Unit = {}