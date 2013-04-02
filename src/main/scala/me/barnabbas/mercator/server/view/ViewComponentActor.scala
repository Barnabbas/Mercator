package me.barnabbas.mercator.server.view

import me.barnabbas.mercator.networking.Messages._
import akka.actor.Actor
import akka.actor.ActorRef
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import akka.dispatch.ExecutionContexts

/**
 * A small wrapper class for ViewComponent, that makes a ViewComponent controlled by an Actor
 */
trait ViewComponentActor extends ViewComponent with Actor {

  private implicit val executionContext = ExecutionContexts.global()

  /**
   * handling the initial connect message from the client
   */
  override def receive = {
    case actor: ActorRef => {

      // setting the client to actor
      clientOpt = Some(actor)

      // going to the running state
      context become running

      // running initialization code
      setup()
    }
    
    case message => println(s"ViewComponentActor: strange message, $message")
  }

  /**
   * The receive method for when we are already running
   */
  protected def running: Receive = {
    case Disconnect => { // stopping this component
      client ! Disconnect
      sender ! "ACK"
      context stop self
    }
    case Update => update() // update request
    case message => {
      if (sender == client) clientEvent(message)
      else serverEvent(message)
    }
  }

}


