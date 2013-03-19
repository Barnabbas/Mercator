package me.barnabbas.mercator.server.area.sidescrolling

/**
 * A Tile is an element that is used to build TileMaps
 */
sealed class Tile

object Tile {

  case object Obstacle extends Tile
  case object Open extends Tile

  /**
   * An Event is a Tile that triggers a certain action.
   * Events are not an obstacle.
   */
  case class Event(action: Entity3D => Unit) extends Tile
  
  
  /**
   * Generate a function of ((Int, Int)) => Tiles by mapping the boolean output to the basic tiles (Obstacle and Open).
   * It will map such that true will become Obstacle and false Open
   */
  def toTiles(tiles: ((Int, Int, Int)) => Boolean): PartialFunction[((Int, Int, Int)), Tile] = {
    PartialFunction({ b: Boolean =>
      if (b) Obstacle
      else Open
    } compose tiles)
  }

  /**
   * Returns a base to build a TileMap over.<br>
   * The given tiles contains a fully defined functions with an open area build upon {@code floor}.
   * @param floor the height of the floor - where the players can walk on
   * @param width the width of the path, until we walk against the walls
   * @param depth the depth of the path
   */
  def baseTiles(floor: Int, width: Int, depth: Int) = toTiles { t: (Int, Int, Int) =>
    val (x, y, z) = t
    y < floor || x < 0 || width <= x || z < 0 || depth <= z
  }

}