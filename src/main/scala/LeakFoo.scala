import scala.util.NotGiven
type LeakFoo[T] = core.LeakFoo[T]
val ok = summon[NotGiven[LeakFoo[1] =:= LeakFoo[2]]]