val myOpaque: MyOpaque[8] = ???
val container = myOpaque.getContainer
//val shouldWork = summon[container.type <:< Container[8]]