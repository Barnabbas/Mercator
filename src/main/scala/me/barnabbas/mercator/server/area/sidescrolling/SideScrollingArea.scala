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
import scala.collection.mutable.HashMap

abstract class SideScrollingArea extends BaseArea {

  /** the tiles of this map */
  private val _tileMap = tileMap.start(context)

  /** the entities of this Map */
  private var _entities: Set[Entity3D] = Set.empty

  /** the components */
  private val components = Seq(Area3DView(this))

  /**
   * The View for the players
   */
  private val views = new HashMap[Entity3D, View[Entity3D]]

  /**
   * An iterator to generate Ids for the Entities. This generator will always have a next.
   */
  private val idGenerator = Stream.from(0).iterator

  /** An identifier for this SideScrollingArea, used for choosing the view */
  def identifier: String

  /**
   * Creates a TileMap for this Area.
   */
  def tileMap: TileMap

  /**
   * The entities that are currently on this Map
   */
  def entities = _entities
  
  /**
   * Returns the entity with the given id if this entity is in this area.
   */
  def entity(id: Any) = entities find (_.id == id)

  /**
   * Creates an entity and places in this area at the given location
   */
  protected def enterEntity(data: EntityData, location: Point3D) {

    // creating the entity
    val id = synchronized(idGenerator.next())
    val props = TypedProps(classOf[Entity3D], new Entity3DImpl(data, location, id))
    val actorName = "entity" + id.toString
    val entity = TypedActor(context).typedActorOf(props, actorName)

    // adding the entity to this map
    _entities += entity

    // creating a view for the entity if there is a client connected to
    for (client <- data.client) {
      val view = View(components, entity)
      views += entity -> view
      view connect client
    }
  }

  /**
   * Removes and shutdowns the given entity and informs the manager that the entity left through the given Gate.
   */
  protected def leaveEntity(entity: Entity3D, gate: Gate) = {
    val data = entity.data

    synchronized {
      require(_entities contains entity, s"$entity is not in this Area")
      _entities -= entity
    }

    // updating views
    val view = views(entity)
    views -= entity

    // shutting down everything after the entity left
    implicit val executionContext = ExecutionContexts.global()
    view.disconnect() foreach { _ =>
      TypedActor(context) stop entity
      TypedActor(context) stop view
      super.leaveEntity(data, gate)
    }
  }

  override def toString() = identifier

  // updating the entities
  Updater { time =>
    for (entity <- _entities) entity.update(time, _tileMap)
  }

}