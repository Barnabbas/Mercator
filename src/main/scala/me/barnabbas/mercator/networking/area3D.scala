package me.barnabbas.mercator.networking

  /**
   * An entity that exists in the 3D
   */
  class Entity(val location: (Float, Float, Float), val appearance: String, val id: Any) extends Serializable
  /**
   * A set of entities
   * @param entities the entities in this area (including the player)
   * @param player the id of the player if there is one in this Area
   */
  class AreaSet(val entities: Set[Entity], val player: Option[Any]) extends Serializable