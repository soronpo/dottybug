package DFiant.core

object compiler:
  object ir:
    sealed trait DFType
    sealed trait DFBoolOrBit extends DFType
