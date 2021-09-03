trait Container[T]
opaque type MyOpaque[W <: Int] = Int
extension [W <: Int](myOpaque: MyOpaque[W]) def getContainer: Container[W] = ???