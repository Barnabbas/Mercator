package me.barnabbas.mercator.client.networking

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import me.barnabbas.mercator.networking.Messages._
import me.barnabbas.mercator.networking.Description
import me.barnabbas.mercator.networking.Description._
import me.barnabbas.mercator.client.Application

/**
 * A Networker will communicate with the server and create new NetworkerComponents to manage the communication with the Server.
 */
class Networker extends Actor {

  override def receive = {
    // got start request
    case Networker.Start(server) => {
      server ! ("Stef", self)
    }

    case Connect(actor, description) => {
      
      // creating the component
      val networkerComponent = description match {
        case SideScrolling3D(id) => context.actorOf(Props[SideScrollingNetworker])
        case ControlListener => context.actorOf(Props[ControllerNetworker])
        case Text => ???
      }
      
      // sending start message
      networkerComponent ! NetworkerComponent.Start(actor, description)
    }

    case "exit" => Application.shutdown()
  }

}

object Networker {
  /**
   * Starts the Networker
   */
  case class Start(server: ActorRef)
}