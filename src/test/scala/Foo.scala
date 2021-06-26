object Foo:

  val x  = summon[DFBits2.TC[DFBits2[7], DFBits2[9]]]
  summon[x.Out =:= Samurai.Check[7, 9]]
