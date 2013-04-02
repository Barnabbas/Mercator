package me.barnabbas.mercator.server.view

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.IO.Connect
import Actor.Receive
import akka.actor.ActorContext
import me.barnabbas.mercator.networking.Description
import me.barnabbas.mercator.networking.Messages
import me.barnabbas.mercator.server._

/**
 * A ViewComponent is a part of a {@link View}.
 * It is an interface between one section of the model and one section of the View of the Client.
 * @author Barnabbas
 *
 */
trait ViewComponent {

  /**
   * Receive events from the client this component is connected to.
   * Should be overridden; by default it gives an error when it receives a message.
   */
  protected def clientEvent: Receive = notImplemented("clientEvent")

  /**
   * Receive events from other parts of the Server.
   * Should be overridden; by default it gives an error when it receives a message.
   */
  protected def serverEvent: Receive = notImplemented("serverEvent")

  /**
   * Updates the client with information about the state of the Area.
   */
  protected def update(): Unit = notImplemented("update")

  /**
   * Code that must be run on initializing
   */
  protected def setup(): Unit = {
    // default is no operation
  }

  /**
   * The Client this ViewComponent is connected to.
   */
  private[view] var clientOpt: Option[Client] = None

  /**
   * The Client this ViewComponent is connected to.
   * @throws IllegalStateException if no Client is connected (yet).
   */
  protected final def client =
    clientOpt getOrElse (throw new IllegalStateException("No client connected yet"))

  /**
   * The default implementation when the receive functions are not implemented
   * @param function the name of the function (used in error message)
   */
  private def notImplemented(function: String): Receive = {
    case message =>
      throw new UnsupportedOperationException(s"$function is not implemented; received $message")
  }

}