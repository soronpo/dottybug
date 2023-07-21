extension [L](lhs: L)(using icL: Candidate[L]) def extend2: DFBits[Int] = ???
object Test:
  val x: DFBits[8] = ???
  val z: DFBits[Int] = x.extend2
  summon[Candidate[z.type]]
