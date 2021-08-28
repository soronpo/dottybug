package core

opaque type Foo[T] <: Int = Int
type LeakFoo[T] = Foo[T]

