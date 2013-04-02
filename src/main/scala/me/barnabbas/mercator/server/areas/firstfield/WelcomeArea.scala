package me.barnabbas.mercator.server.areas.firstfield

import me.barnabbas.mercator.server.EntityData
import me.barnabbas.mercator.server.area.Gate
import me.barnabbas.mercator.server.area.ManagerArea
import me.barnabbas.mercator.server.area.ManagerArea.AreaRef
import me.barnabbas.mercator.server.area.sidescrolling.Entity3D
import me.barnabbas.mercator.server.area.sidescrolling.Point.Point3D
import me.barnabbas.mercator.server.area.sidescrolling.SideScrollingArea
import me.barnabbas.mercator.server.area.sidescrolling.TileMap
import scala.concurrent.Future
import akka.dispatch.ExecutionContexts
import me.barnabbas.mercator.server.area.sidescrolling.Tile
import me.barnabbas.mercator.server.area.sidescrolling.TileMapImpl
import me.barnabbas.mercator.server.EntityData
import me.barnabbas.mercator.server.area.sidescrolling.Point3D

class WelcomeArea extends ManagerArea {
	import WelcomeArea._
	
	val mainArea = create[MainArea]("field")
	val houseArea = create[HouseArea]("house")

	protected override def onEntityEnter(entity: EntityData, gate: Gate) = gate match {
      case Gate.LogIn => enter(entity, mainArea, gate)
//      case Gate.LogIn => enter(entity, houseArea, FieldHouseGate)
    }
	
	protected override def onEntityLeftSubArea(entity: EntityData, area: AreaRef, gate: Gate) = gate match {
	  case FieldHouseGate => enter(entity, houseArea, FieldHouseGate)
	  case HouseFieldGate => enter(entity, mainArea, HouseFieldGate)
	  case Gate.LogOut => leaveEntity(entity, Gate.LogOut)
	}
}

object WelcomeArea {

  private case object FieldHouseGate extends Gate
  private case object HouseFieldGate extends Gate

  // the main area
  private class MainArea extends SideScrollingArea {
    def identifier = "welcome.field"
    def tileMap = {
      val event = Tile.Event(entity => enterHouse(entity))

      val house: PartialFunction[(Int, Int, Int), Tile] = {
        case (27, y, 10) if 15 <= y => Tile.Open
        case (28, y, 10) if 15 <= y => Tile.Open
        case (27, y, 11) if 15 <= y && y < 19 => event
        case (28, y, 11) if 15 <= y && y < 19 => event
      }

      val obstacle = new PartialFunction[(Int, Int, Int), Tile]() {
        def isDefinedAt(tuple: (Int, Int, Int)) = {
          val (x, y, z) = tuple
          (15 until 20 contains x) && y < 19
        }
        def apply(tuple: (Int, Int, Int)) = Tile.Obstacle
      }

      val ground = Tile.baseTiles(15, 80, 10)
      TileMap(house orElse obstacle orElse ground, 10, 10, 10)
    }

    protected override def onEntityEnter(entity: EntityData, gate: Gate) = gate match {
      case Gate.LogIn => enterEntity(entity, Point3D(20, 180, 20))
      case HouseFieldGate => enterEntity(entity, Point3D(270, 150, 80))
    }

    private def enterHouse(entity: Entity3D) {
      leaveEntity(entity, FieldHouseGate)
    }
  }

  private class HouseArea extends SideScrollingArea {
    def identifier = "welcome.house" // currently named welcome.field (just to make it easier)
    def tileMap = {
      val event = Tile.Event(leaveEntity(_, HouseFieldGate))
      
      val roof: PartialFunction[(Int, Int, Int), Tile] = {
        case (_, 23, _) => Tile.Obstacle
      }
      
      val exit: PartialFunction[(Int, Int, Int), Tile] = {
        case (x, y, -1) if 15 <= y && (13 to 15 contains x) => event
      }
      
      val ground = Tile.baseTiles(15, 30, 10)
      
      TileMap(roof orElse exit orElse ground, 10, 10, 10)
    }

    protected override def onEntityEnter(entity: EntityData, gate: Gate) = gate match {
      case FieldHouseGate => enterEntity(entity, Point3D(140, 150, 20))
    }
    
    // adding some entities to this Area
    enterEntity(new EntityData("Strange guy", None), new Point3D(70, 150, 40))
    
  }

}