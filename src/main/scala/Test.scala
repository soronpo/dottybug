import mylib.*
import Import.*
object Test:
  val oneFail : 1 = exported //error
  val oneWork : 1 = imported //works
