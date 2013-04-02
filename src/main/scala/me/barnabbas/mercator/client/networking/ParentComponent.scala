package me.barnabbas.mercator.client.networking

import me.barnabbas.mercator.networking.Messages.Connect
import me.barnabbas.mercator.networking.Description
import akka.actor.ActorRef

/**
 * A NetworkerComponent that can create new NetworkerComponents.
 */
trait ParentComponent extends NetworkerComponent {

  /**
   * Connects a new NetworkerComponent that will become a child of this Component
   */
  protected def connectComponent(connect: Connect)

  override def receive = {
    val connectReceive: PartialFunction[Any, Unit] = {
      case connect: Connect => connectComponent(connect)
    }
    connectReceive orElse super.receive
  }

}