package me.barnabbas.mercator.server.area

import me.barnabbas.mercator.server.EntityData
import akka.actor.Actor
import akka.actor.ActorRef
import AreaMessage._

/**
 * The base Area, the parent of most Areas.
 * Supports basic functionalities like managing entering and leaving of Entities. <br>
 * It uses the following protocol: <br>
 * 
 * - first it wants the ActorRef of the manager, it will use this to listen for enter and leaves <br>
 * - now it will listen to EntityEnters and handle those <br>
 * - it sends EntityLeave messages when an entity wants to leave 
 */
abstract class BaseArea extends Area {
  
  /**
   * The manager of this Area, this will be notified when a player leaves the Map
   */
  private var areaManager = Option.empty[ActorRef]
  
  /**
   * The initial state, waiting for the manager to send its ActorRef
   */
  final override def receive = {
    case manager: ActorRef => {
      areaManager = Some(manager)
      context.become(entityListener)
    }
  }
  
  /**
   * The main state of an Area, waiting for information from the manager.
   */
  protected def entityListener: Receive = {
    case EntityEnter(entity, gate) => onEntityEnter(entity, gate)
  }

  
  /**
   * Removes an Entity from this Map.<br>
   * @param entity the new EntityData for the entity that just left
   */
  protected final override def leaveEntity(entity: EntityData, gate: Gate) = areaManager match {
    case None => throw new IllegalStateException("This Area is not ready yet")
    case Some(manager) => manager ! EntityLeave(entity, gate)
  }
}