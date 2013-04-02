package me.barnabbas.mercator.server.area.sidescrolling

import me.barnabbas.mercator.server.EntityData
import Entity3D._
import akka.actor.Actor

/**
 * A two dimensional entity
 */
trait Entity3D {

  def location: Point3D
  
  /**
   * The id of this Entity. This is unique for the map this Entity lives in.
   */
  def id: Int

  /**
   * Makes this Entity move into {@code direction}.
   */
  def move(direction: Direction)

  /**
   * Makes this Entity stop moving.
   */
  def stop()

  /**
   * Makes this Entity jump.
   */
  def jump()
  
  /**
   * let {@code entity} interact with this.
   */
  def interact(entity: Entity3D)

  private[sidescrolling] def update(time: Int, tileMap: TileMap)
  private[sidescrolling] def data: EntityData
  
  override def toString = s"Entity ${data.name}"
}

object Entity3D {

  /**
   * The Directions a Entity can move into. Note that Up and Down are still not in the y-direction
   */
  type Direction = Direction.Value
  object Direction extends Enumeration {
    val Left, Right, Up, Down = Value
    val UpLeft, UpRight, DownLeft, DownRight = Value
  }

}