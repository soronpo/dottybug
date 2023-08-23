type Of[T] <: DFTypeAny = T match
  case DFTypeAny => T <:! DFTypeAny

