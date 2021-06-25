object Foo:
  val fails = summon[Bar[2] =:= "2"]
