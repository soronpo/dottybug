type DFBits = DFType

object DFBits:
  def apply(width: Any): Unit = {}
  val y: DFToken = ???
  y.width

  trait Candidate
  object Candidate:
    given Candidate = ???

    import DFVal.bits
    val x: IRToken = ???
    x.bits

  object Conversions:
    implicit def conv[R](from: R)(using Candidate): DFVal = ???
end DFBits
