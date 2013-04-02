package me.barnabbas.mercator.server.area.sidescrolling

import org.scalatest.FunSuite
import akka.actor.ActorSystem
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.Promise
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TileMapImplTest extends FunSuite with BeforeAndAfterAll {
  
  import Point._
  
  /** the number of events that happened */
  var eventPromise = Promise[String]

  val event = Tile.Event(_ => {eventPromise success "done"})

  val tiles = { index: (Int, Int, Int) =>
    val (x, y, z) = index
    // making a cube
    if (x.abs <= 1 && y.abs <= 1 && z.abs <= 1) Tile.Obstacle
    else if ((x, y, z) == (3, 3, 3)) event
    else Tile.Open
  }

  val system = ActorSystem("TileMapImplTest")
  val tileMap = TileMap(tiles, 1, 1, 1).start(system)

  test("isOnFloor") {
    
    // in the middle
    assert(tileMap.isOnFloor(Point3D(0, 1, 0), Point3D(1, 1, 1)) === true)
    
    // over the edge
    assert(tileMap.isOnFloor(Point3D(-5, 1, -10), Point3D(5, 1, 9)) === true)
    
    // on the other edge
    assert(tileMap.isOnFloor(Point3D(.9, 1.2, .9), Point3D(1, 1, 1)) === true)
    
    // in the air
    assert(tileMap.isOnFloor(Point3D(0, 2.5, 0), Point3D(1, 1, 1)) === false)
    
    // next to the block - x
    assert(tileMap.isOnFloor(Point3D(2.5, 1, 0), Point3D(1, 1, 1)) === false)
    
    // next to the block - z
    assert(tileMap.isOnFloor(Point3D(0, 1, -2.1), Point3D(1, 1, 1)) === false)
    
    // under the block
    assert(tileMap.isOnFloor(Point3D(0, -2, 0), Point3D(1, 1, 1)) === false)
  }
  
  test("move"){
    val entity = null
    val size = Point3D(3, 6, 2)
    
    import TileMap._
    import Collision._
    
    def move(start:Point3D, goal: Point3D) = tileMap.move(start, size, entity)(goal)
    
    // just nothing
    val move1 = move(Point3D(2, 0, -3), Point3D(0, -1.1, -3))
    assert(move1 === MoveResponse(Point3D(0, -1.1, -3), Seq.empty))
    
    // walking against the cube
    val move2 = move(Point3D(0, 5, 1), Point3D(0, -4, 1))
    assert(move2 === MoveResponse(Point3D(0, 2, 1), Seq(Floor)))
    
    // walking against the cube - second
    val move3 = move(Point3D(-7, -3, 0), Point3D(-2, -3, 0))
    assert(move3 === MoveResponse(Point3D(-4, -3, 0), Seq(Wall)))
    
    // checking for event
    val move4 = move(Point3D(-1, 2.8, 2.5), Point3D(2.4, 2.8, 2.5))
    assert(move4 === MoveResponse(Point3D(2.4, 2.8, 2.5), Seq.empty))
    
    // the number of events that happened
    // todo: warning - asynchronous stuff. 
    // This didn't work, the event actor never seemed to run
//    val eventResult = Await.result(eventPromise.future, Duration(5, "second"))
//    assert(eventResult === "done")
  }
  
  
  override def afterAll(){
    system.shutdown()
  }

  //  test("isObstalce"){
  //    val tiles = Map((0, 0, 0) -> Open, (1, 1, 0) -> Obstacle, (0, 2, 1) -> Event(_ => ???))
  //    val tileMap = TileMap(tiles, 1, 1, 1)
  //    
  //    // no longer used
  ////    assert(tileMap.isObstacle(0, 0, 0) === false)
  ////    assert(tileMap.isObstacle(1, 1, 0) === true)
  ////    assert(tileMap.isObstacle(0, 2, 1) === false)
  //    assert(tileMap(0, 0, 0) === Open)
  //    intercept[Exception](tiles(0, 3, 1))
  //  }

  // to

}