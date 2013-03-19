package me.barnabbas.mercator.networking

/**
 * Contains classes for receiving communication about player input
 */
object Input {
  
  case class KeyPressed(key: Key)
  case class KeyReleased(key: Key)
  
  type Key = Key.Value
  
  /**
   * The Keys that are used for communication. Those are only action keys, not really the keyboard keys.
   */
  object Key extends Enumeration {
    val Left, Right, Up, Down, Jump = Value
  }

}