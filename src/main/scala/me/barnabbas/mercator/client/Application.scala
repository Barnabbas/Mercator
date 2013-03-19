package me.barnabbas.mercator.client

import me.barnabbas.mercator.client.networking.Networker
import me.barnabbas.mercator.server.Server
import me.barnabbas.mercator.client.view.View

import akka.actor.Props
import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory

/**
 * The Application class of the Client.
 * Will start the View and the Networker. At the moment this also starts the Server
 */
object Application extends Bootable with App {

  private val actorSystem = ActorSystem("Application")

  // the View
  View.start()

  // todo: changed for testing
  Server.main(Array.empty)
  
  // the Server
//  private val server = actorSystem.actorFor("akka://mercator@localhost:3225/user/server")
  private val server = Server.actor
  
  // the Networker
  private val networker = actorSystem.actorOf(Props[Networker], "Networker")
  networker ! Networker.Start(server)

  def startup() = {}
  
  /**
   * Closes the application
   */
  def shutdown() = {
    println("Closing the Application")
    View.exit()
    println("View closed")
    server ! "exit"
    println("Server close request send")
    actorSystem.shutdown()
  }
}