package core

opaque type Foo <: Int = Int
type LeakFoo[M] = M match
  case _ => Foo

