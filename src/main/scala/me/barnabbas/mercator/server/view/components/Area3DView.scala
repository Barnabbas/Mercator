package me.barnabbas.mercator.server.view.components

import me.barnabbas.mercator.server.area.sidescrolling._
import me.barnabbas.mercator.server.view.ViewComponentFactory
import me.barnabbas.mercator.server.view.ViewComponent
import me.barnabbas.mercator.networking.Description
import me.barnabbas.mercator.networking.AreaSet
import me.barnabbas.mercator.server.view.ViewComponentActor
import me.barnabbas.mercator.server.view.ViewComponentParent
import me.barnabbas.mercator.server.controller.PlayerController
import me.barnabbas.mercator.server.view.ViewComponentParentActor

/**
 * A ViewComponent used to display 2D areas.
 * Currently this class is created for the SideScrollingArea, but this will be generised later.
 */
class Area3DView(entity: Entity3D, area: SideScrollingArea) extends ViewComponentParent {

  import Area3DView._

  def childrenFactories = {
    val playerController = PlayerController(area)
    Seq((() => playerController(entity)) -> playerController.description)
  }

  override def update = {
    //todo: entity is null?
    val areaEntities = area.entities

    val viewEntities = for (ent <- areaEntities) yield transformEntity(ent)
    val player = {
      if (areaEntities contains entity) Some(entity.id)
      else None
    }
    val areaSet = new AreaSet(viewEntities, player)
    client ! areaSet
  }
}

object Area3DView {

  /**
   * Transforms an Entity3D to a networking entity.
   * Also added in try-catch, because this often gave errors.
   */
  private def transformEntity(entity: Entity3D) = try {
    val loc = entity.location map (f => f)
    new me.barnabbas.mercator.networking.Entity(loc, "", entity.id)
  } catch {
    case e: Exception => sys.error(s"something wrong with $entity: $e")
  }

  /**
   * Creates a new ViewComponentFactory for {@code area}.
   */
  def apply(area: SideScrollingArea) =
    ViewComponentFactory[Entity3D](Description.SideScrolling3D(area.identifier)) { entity =>
      new Area3DView(entity, area) with ViewComponentParentActor
    }
}