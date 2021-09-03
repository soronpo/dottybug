val myOpaque: MyOpaque[8] = ???
val container = myOpaque.getContainer
//after successful compilation, uncomment the following line to get the error
//val shouldWork = summon[container.type <:< Container[8]]