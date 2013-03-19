package me.barnabbas.mercator.client.networking

import akka.actor.Actor
import akka.actor.ActorRef
import me.barnabbas.mercator.client.view.Renderer
import me.barnabbas.mercator.client.view.View
import me.barnabbas.mercator.networking.Messages._
import me.barnabbas.mercator.networking.Description

/**
 * A Framework to make implementing a NetworkerComponent easier
 */
abstract class NetworkerComponent extends Actor {

  private var _server: ActorRef = _
  private var renderer: Renderer = _

  final override def receive = {
    case Connect(actor, description) => {
      actor ! self
      _server = actor
      renderer = renderer(description)
      View += renderer
    }
    case Disconnect => {
      View -= renderer
      context stop self
    }
    case x => receiveMessage(x)
  }

  /**
   * The actor of the server this Component is connected with
   */
  protected def server = {
    if (_server == null) throw new IllegalStateException("This component is not connected yet.")
    else _server
  }

  /**
   * The Renderer this NetworkerComponent use to display itself
   */
  def renderer(description: Description): Renderer

  /**
   * Will be called when a message has been send from the server
   */
  def receiveMessage: Actor.Receive
}