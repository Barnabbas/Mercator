package me.barnabbas.mercator.server

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory

import me.barnabbas.mercator.server.area.AreaMessage
import me.barnabbas.mercator.server.area.Gate
import me.barnabbas.mercator.server.areas.firstfield.WelcomeArea

/**
 * The main object for the Server.<br>
 * This will start the areas of the server and will manage players logging in and out.
 */
object Server extends Bootable with App {
	
  private val actorSystem = ActorSystem("mercator")

  /** The area where players are send to for log in */
  private val mainArea = actorSystem.actorOf(Props[WelcomeArea], "MainArea")
  

  // starting the actor we use to communicate with the clients
  // todo: remove actor again, only for testing
  val actor = actorSystem.actorOf(Props[ServerActor], "server")

  /**
   * An Actor that handles connection requests from the Clients.
   * This is the main Actor that will be connected to.
   */
  private class ServerActor extends Actor {

    override def preStart() = mainArea ! self

    override def receive = {

      // log in request
      case (name: String, client: ActorRef) => {
        val data = new EntityData(name, Some(client))
        println(s"Server: $name just entered the server")
        mainArea ! AreaMessage.EntityEnter(data, Gate.LogIn)
      }
      case AreaMessage.EntityLeave(entity, Gate.LogOut) => entity.client foreach (_ ! "exit")
      
      // todo: only temporary, to be able to exit this Server in a normal way
      case "exit" => shutdown()
    }
  }

  def startup() = {}

  def shutdown() = {
    println("Closing the Server")
    actorSystem.shutdown()
  }
  
//  todo: can't get readline working :/
//  /**
//   * This keeps reading the input until "exit" is entered. Then it will shutdown the server
//   */
//  private def listen(): Unit = {
//    readLine() match {
//      case "exit" => shutdown()
//      case _ => listen()
//    }
//  }
}