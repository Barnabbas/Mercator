package me.barnabbas.mercator.networking

/**
 * Contains classes for receiving communication about player input
 */
object Input {
  
  case class KeyPressed(key: Key)
  case class KeyReleased(key: Key)
  /**
   * An notification that the client want to interact with an entity
   * @param id the id of the entity
   */
  case class Interact(id: Any)
  
  type Key = Key.Value
  
  /**
   * The Keys that are used for communication. Those are only action keys, not really the keyboard keys.
   */
  object Key extends Enumeration {
    val Left, Right, Up, Down, Jump = Value
  }

}