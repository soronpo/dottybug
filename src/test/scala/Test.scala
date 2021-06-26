object Test:

  val x  = summon[Lie.TC[Lie[7], Lie[9]]]
  val fails = summon[x.Out =:= Samurai.Check[7, 9]]
