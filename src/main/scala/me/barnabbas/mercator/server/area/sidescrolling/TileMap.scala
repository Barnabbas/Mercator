package me.barnabbas.mercator.server.area.sidescrolling

import akka.actor.ActorRefFactory

/**
 * A TileMap is a Map that is used to determine where there are obstacles on the map.
 */
trait TileMap {
  
  /**
   * Used by SideScrolling Area to add a context to it.
   */
  private[sidescrolling] def start(factory: ActorRefFactory): TileMap
  
  /**
   * Determines if the given entity is on the floor
   * @param location the location of the entity
   * @param size the size of the entity
   */
  def isOnFloor(location: Point3D, size: Point3D): Boolean

  /**
   * Tries to find the new location for the given entity.
   * @param location where the Entity currently is
   * @param size the size of the entity
   * @param entity the Entity that is moving (this is used for the events)
   * @param goal where the entity wants to go to
   */
  def move(location: Point3D, size: Point3D, entity: Entity3D)(goal: Point3D): TileMap.MoveResponse
}

object TileMap {

  def apply(tiles: ((Int, Int, Int)) => Tile, tileWidth: Int, tileHeight: Int, tileDepth: Int) =
    TileMapImpl(tiles, tileWidth, tileHeight, tileDepth)

  /**
   * Response to a move request, contains the new location and the collisions it made
   */
  final case class MoveResponse(newLocation: Point3D, collisions: Seq[Collision.Value])

  object Collision extends Enumeration {
    val Floor, Ceiling, Edge, Wall = Value
  }

}