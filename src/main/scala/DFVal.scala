package dfhdl

// Compiled in stage 1 and then left untouched, so in stage 2 it is loaded
// from TASTy. Its `export` re-exports a given that is actually *defined* in a
// different file (DFXInt.Ops in stubs.scala) which is being recompiled.
object DFVal:
  export DFXInt.Ops.c1
  object Ops:
    type CarryOp
