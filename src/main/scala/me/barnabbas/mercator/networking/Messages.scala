package me.barnabbas.mercator.networking

import akka.actor.ActorRef

/**
 * The messages used for networking in this application
 */
object Messages {

  /**
   * A Message to connect a ViewComponent with a ClientComponent
   */
  case class Connect(actor: ActorRef, description: Description)
  /**
   * A Message to disconnect a ViewComponent with a ClientComponent.
   */
  case object Disconnect
  
  /**
   * This is used to request an update.
   * Normally the Client will send this message to expect update information back.
   */
  case object Update
  
  object CommandView {
    /**
     * A message indicating that the View can be opened again for listening to commands
     */
    case object Open
    
    /**
     * A message indicating that a user wants to use auto-complete for the given String
     */
    case class Complete(string: String)
  }
  
}