package dfhdl
// hdl.scala must be recompiled *together with* stubs.scala (as a second source
// unit) for the bug to survive the #25900 fix. The top-level `export hdl.*`
// re-exports the DFXInt.Ops givens at package level, which is what makes the
// cross-unit resolution path differ from the single-unit case that #25900 fixed.
object hdl:
  export DFDecimal.Ops.*

export hdl.*
