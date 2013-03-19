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
      require(server != null, "Server must be initialised!")
      server ! ("Stef", self)
    }
    case connect @ Connect(actor, description) => description match {
      case SideScrolling3D(id) => {
        val networker = context.actorOf(Props[SideScrollingNetworker])
        networker ! connect
      }
      case Text => ???
      case ControlListener => {
        val networker = context.actorOf(Props[ControllerNetworker])
        networker ! connect
      }
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