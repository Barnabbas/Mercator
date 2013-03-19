package me.barnabbas.mercator

/**
 * The main package. Contains the classes that are used to start this Application.
 */
import akka.actor.ActorRef
package object server{
  
  /**
   * A Client is a reference to the Actor of the Client
   */
  type Client = ActorRef
  
  
}