package me.barnabbas.mercator.server.area.sidescrolling

import akka.actor.TypedActor
import akka.actor.TypedProps
import me.barnabbas.mercator.server.area.BaseArea
import me.barnabbas.mercator.server.area.Gate
import me.barnabbas.mercator.server.area.Updater
import me.barnabbas.mercator.server.controller.PlayerController
import me.barnabbas.mercator.server.view.components.Area3DView
import me.barnabbas.mercator.server.EntityData
import me.barnabbas.mercator.server.view.View
import akka.dispatch.ExecutionContexts

abstract class SideScrollingArea extends BaseArea {

  /** An identifier for this SideScrollingArea, used for choosing the view */
  def identifier: String
  
  /**
   * Creates a TileMap for this Area.
   */
  def tileMap :TileMap

  /** the tiles of this map */
  private val tileMapActor = {
    val props = TypedProps(classOf[TileMap], tileMap)
    TypedActor(context).typedActorOf(props, "tileMap")
  }

  /** the entities of this Map */
  private var entitiesVar: Set[Entity3D] = Set.empty

  /** the components */
  private val components = Map('area -> Area3DView(this), 'controller -> PlayerController())

  /**
   * The entities that are currently on this Map
   */
  def entities = entitiesVar

  // updating the entities
  Updater { time =>
    for (entity <- entitiesVar) entity.update(time, tileMapActor)
  }

  // todo: make something better from this
  var view: View[Entity3D] = _

  /**
   * Creates an entity and places in this area at the given location
   */
  protected def enterEntity(data: EntityData, location: Point3D) {
    val props = TypedProps(classOf[Entity3D], new Entity3DImpl(data, this, location))
    val entity = TypedActor(context).typedActorOf(props, "entityActor")
    entitiesVar += entity
    view = View(components, entity)
    view connect data.client
  }

  /**
   * Removes and shutdowns the given entity and informs the manager that the entity left through the given Gate.
   */
  protected def leaveEntity(entity: Entity3D, gate: Gate) = {
    val data = entity.data
    
    synchronized{
    	require(entitiesVar contains entity, s"$entity is not in this Area")
    	entitiesVar -= entity
    }

    // shutting down everything after the entity left
    implicit val executionContext = ExecutionContexts.global()
    view.disconnect() foreach { _ =>
      TypedActor(context) poisonPill entity
      super.leaveEntity(data, gate)
    }
  }

  override def toString() = identifier

}