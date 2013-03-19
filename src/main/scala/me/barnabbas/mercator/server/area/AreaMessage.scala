package me.barnabbas.mercator.server.area

import me.barnabbas.mercator.server.EntityData

/**
 * A Message that is used to send data between an Area and its Manager
 * @author Barnabbas
 */
sealed class AreaMessage

object AreaMessage {
  /**
   * A Message indicating that {@code entity} is entering the Area
   * @param entity the Entity that wants to enter
   * @param gate the Gate through which {@code entity} wants to enter
   */
  case class EntityEnter(entity: EntityData, gate: Gate)
  /**
   * A Message indicating that {@code entity} is leaving the Area
   * @param entity the Entity that wants to enter
   * @param gate the Gate through which {@code entity} wants to leave
   */
  case class EntityLeave(entity: EntityData, gate: Gate)
}