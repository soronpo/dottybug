val shouldFail: LeakFoo[Any] = 1
val shouldWork = summon[LeakFoo[Any] =:= core.Foo]