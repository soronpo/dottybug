trait DFBits[W <: Int]

trait Candidate2[R]:
  type OutW <: Int
object Candidate2:
  given fromDFBits[W <: Int, R <: DFBits[W] <:> VAL]: Candidate2[R] with
    type OutW = W
