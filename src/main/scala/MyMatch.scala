opaque type DFType = Int
sealed trait DFEncoding
class DFString extends DFEncoding

type MyMatch[T <: DFType | DFEncoding] = T match
  case DFType     => Int
  case DFEncoding => String

type MyMatch2[T <: DFType2 | DFEncoding] = T match
  case DFType2     => Int
  case DFEncoding => String

val works = summon[MyMatch[DFString] =:= String]
val fails = summon[MyMatch2[DFString] =:= String]
