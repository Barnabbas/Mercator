package me.barnabbas.mercator.server.area.sidescrolling

import me.barnabbas.mercator.server.area.sidescrolling.Point._
import akka.actor.Actor
import akka.actor.TypedActor
import akka.actor.Props
import akka.actor.ActorRef
import akka.actor.ActorContext
import akka.actor.ActorRefFactory

/**
 * Simple implementation for the TileMap trait.
 * This TileMap must be started before using it.
 */
private[sidescrolling] class TileMapImpl private (tiles: ((Int, Int, Int)) => Tile,
    val tileWidth: Int, val tileHeight: Int, val tileDepth: Int) extends TileMap {

  import TileMapImpl._
  import Tile._
  
  /** The actor to handle the events (this must be done asynchronous) */
  private var eventActor: ActorRef = _
  
  private[sidescrolling] def start(factory: ActorRefFactory): TileMap = {
    eventActor = factory.actorOf(Props[EventActor], "TileMapEventActor")
    this
  }
  

  def isOnFloor(location: Point3D, size: Point3D) = {
    // todo: use all ground under it (like is done for real checking of obstacles)

    
    val (x, _, z) = location map (_.floor.toInt)
    val (width, _, depth) = size.toInts
    
    // the index for y (just a little lower then his real location)
    val j = div((location.y - (size.y / 10)).toInt, tileHeight)
    val tileVals = for (
      i <- div(x, tileWidth) to div(x + width, tileWidth);
      k <- div(z, tileDepth) to div(z + depth, tileDepth)
    ) yield (i, j, k) -> tiles(i, j, k)
    
    tileVals.unzip._2 contains Obstacle
  }

  def move(location: Point3D, size: Point3D, entity: Entity3D)(goal: Point3D) = {
    
    require(eventActor != null, "This TileMap is not started by the area yet")

    // getting data
    var (x, y, z) = location.toInts
    val (width, height, depth) = size.toInts
    val (dx, dy, dz) = location.map(goal)((a, b) => (b - a)).toInts

    /**
     * todo: hard to explain
     * @return a new value for the element that is first in the tuple
     * combined with whether we hit an obstacle in this direction
     */
    def calculateNew(transform: Transform, reverse: Transform): (Float, Boolean) = {

      /** Gives a type to transform */
      def use[T](f: Transform)(in: (T, T, T)): (T, T, T) = {
        val (xt, yt, zt) = f(in._1, in._2, in._3)
        (xt.asInstanceOf[T], yt.asInstanceOf[T], zt.asInstanceOf[T])
      }

      /**
       * Takes the first element of the given values after transforming it.
       */
      def take[T](x: T, y: T, z: T): T = use(transform)(x, y, z)._1

      /**
       * Method to use take with a tuple
       */
      def takePoint(point: Point3D) = take[Float](point.x, point.y, point.z)

      /** the difference in location based on the given focusElement */
      val da = take(dx, dy, dz)

      if (da != 0) { // we are moving in this direction

        val diff = if (da < 0) 0 else take(width, height, depth)
        //        val (x, y, z) = _loc map (_ toInt)
        val a = take(x, y, z)
        val aTileSize = take(tileWidth, tileHeight, tileDepth)

        // the tiles we are passing
        lazy val collumns = div(x, tileWidth) to div(x + width - 1, tileWidth)
        lazy val rows = div(y, tileHeight) to div(y + height - 1, tileHeight)
        lazy val depths = div(z, tileDepth) to div(z + depth - 1, tileDepth)

        /** what the move in the given direction */
        val bTiles = div(a + diff, aTileSize) to div(a + diff + da, aTileSize) by math.signum(da)
        val indices = {
          val its = (collumns, rows, depths)
          val (it1, it2, it3) = use[Range](transform)(collumns, rows, depths)

          // the iterator, the first is replaced by our custom iterator
          for (i <- bTiles; j <- it2; k <- it3) yield (i, j, k)
        }

        val obstacle = indices find (tile => tiles(use[Int](reverse)(tile)) == Obstacle)

        // the new value
        obstacle match {
          case Some((ia, _, _)) => {
            val tileDiff = if (da < 0) aTileSize else -diff
            (ia * aTileSize + tileDiff, true)
          }
          case None => (takePoint(goal), false)
        }
      } // we are staying in the same place
      else (takePoint(location), false)
    }

    var collisions = List.empty[TileMap.Collision.Value]

    // moving entity -- x
    val (newX, isWallHit) = calculateNew((x, y, z) => (x, y, z), (x, y, z) => (x, y, z))
    x = newX.toInt
    if (isWallHit) collisions ::= TileMap.Collision.Wall

    // moving entity -- y
    val (newY, isFloorOrCeilingHit) = calculateNew((x, y, z) => (y, x, z), (y, x, z) => (x, y, z))
    y = newY.toInt
    if (isFloorOrCeilingHit) {
      collisions ::= (if (dy < 0) TileMap.Collision.Floor
    		  else TileMap.Collision.Ceiling)
    }

    // moving entity -- z
    val (newZ, isEdgeHit) = calculateNew((x, y, z) => (z, x, y), (z, x, y) => (x, y, z))
    z = newZ.toInt
    if (isEdgeHit) collisions ::= TileMap.Collision.Edge
    
    // checking for events on the tiles we collision with
    
    val tileVals = for (
      i <- div(x, tileWidth) to div(x + width, tileWidth);
      j <- div(y, tileHeight) to div(y + height, tileHeight);
      k <- div(z, tileDepth) to div(z + depth, tileDepth)
    ) yield tiles(i, j, k)
    
    tileVals.collectFirst[Unit]{
      case event @ Event(f) => {
        eventActor ! (entity, event)
      }
    }
    
    TileMap.MoveResponse(Point3D(newX, newY, newZ), collisions)
  }
  

}

private[sidescrolling] object TileMapImpl {
  def apply(tiles: ((Int, Int, Int)) => Tile, tileWidth: Int, tileHeight: Int, tileDepth: Int) =
    new TileMapImpl(tiles, tileWidth, tileHeight, tileDepth)

  /** This is used to transform a tuple back to a tuple */
  private type Transform = (Any, Any, Any) => (Any, Any, Any)

  /** real divisor operator */
  private def div(a: Int, b: Int) = (a.toFloat / b).floor.toInt
  
  
  /**
   * This Actor executes events asynchronous
   */
  private class EventActor extends Actor{
    def receive = {
      case (entity: Entity3D, tile: Tile.Event) => {
        val Tile.Event(f) = tile
        f(entity)
      }
    }
  }
}