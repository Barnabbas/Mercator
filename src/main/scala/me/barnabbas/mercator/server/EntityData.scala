package me.barnabbas.mercator.server

import akka.actor.ActorRef

/**
 * Data used to denote an Entity. 
 * This is used for when an Entity is moved from one Area to an other Area.
 * 
 * @author Barnabbas
 */

/**
 * Constructs a new EntityData
 * @param name the name of the EntityData
 * @param client the Actor of the client where the View is connected to
 */
class EntityData(val name: String, val client: Client){
  
  override def toString = "Entity: " + name
  
}