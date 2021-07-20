package DFiant.compiler
package ir

sealed trait DFType
sealed trait DFBoolOrBit extends DFType
case object DFBool extends DFBoolOrBit
