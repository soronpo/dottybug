type LeakFoo[M] = core.LeakFoo[M]

val works = summon[LeakFoo[Any] =:= core.Foo]