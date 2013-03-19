package me.barnabbas.mercator.server.area

import me.barnabbas.mercator.server.EntityData
import akka.actor.Actor

/**
 * An Area is an Actor that manages one area of the game. It must follow the following contract:<br>
 * <ul>
 * <li>At start it must wait for an ActorRef: this is his manager.<br></li>
 * <li>The Manager will send an EntityEnter Message when a new Entity enters the map.<br></li>
 * <li>An Area must send an EntityLeave Message when an Entity has left his map.<br></li>
 * </ul>
 * When a Entity enters an Area and the Entity contains a Client, then a View must be connected to the Client.
 */
trait Area extends Actor {

  /**
   * Will be called when an Entity enters this Area.
   * @param entity the Data of the Entity that wants to enter
   * @param gate the Gate through which it will enter
   */
  protected def onEntityEnter(entity: EntityData, gate: Gate)

  /**
   * Has to be called to let an Entity leave this Area.
   * @param entity the Data of the Entity that wants to enter
   * @param gate the Gate through which it left
   */
  protected def leaveEntity(entity: EntityData, gate: Gate)
}