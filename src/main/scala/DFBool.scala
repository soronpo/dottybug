trait HasToken

opaque type DFBool <: DFType with HasToken = DFType with HasToken

def bar(v: DFVal): Unit = v.as[DFBool]