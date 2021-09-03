val myOpaque: MyOpaque[8] = ???
val container = myOpaque.getContainer
//after successfull compilation, uncomment the following line to get the error
//val shouldWork = summon[container.type <:< Container[8]]