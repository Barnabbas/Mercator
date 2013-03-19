package me.barnabbas.mercator.networking
  /**
   * An entity that exists in the 2D
   */
  class Entity(val location: (Float, Float, Float), val appearance: String) extends Serializable
  /**
   * A set of entities
   * @param entities the entities in this area (excluding player)
   * @param player the character of this player if there is one in this Area
   */
  class AreaSet(val entities: Set[Entity], val player: Option[Entity]) extends Serializable