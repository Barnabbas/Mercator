package me.barnabbas.mercator.server.area

import akka.actor.ActorRef
import me.barnabbas.mercator.server.EntityData
import AreaMessage._
import ManagerArea._
import akka.actor.Props
import akka.actor.Actor

/**
 * ManagerArea is an area that manages other areas.
 * When an entity enters this Area, then it will automatically be added to a subArea of this Manager.
 * This Area also manages where an entity goes to when it leaves one of
 * the subAreas (to another subArea or to leave this ManagerArea)
 *
 * @author Barnabbas
 */
abstract class ManagerArea extends BaseArea {

  /**
   * This will be called when an entity leaves one of the subAreas.<Br>
   * The Entity has to be send to another subArea or has to leave this Area.
   */
  protected def onEntityLeftSubArea(entity: EntityData, area: AreaRef, gate: Gate)

  /**
   * The manager of this Area, this will be notified when a player leaves the Map
   */
  private var areaManager = Option.empty[ActorRef]
  
  /**
   * A map for the AreaRefs
   */
  private var areaRefs = Map.empty[ActorRef, AreaRef]

  /**
   * This will be called for the main Receive function of this ManagerArea
   */
  protected override def entityListener: Receive = super.entityListener orElse {
    case EntityEnter(entity, gate) => onEntityEnter(entity, gate)
    case EntityLeave(entity, gate) => onEntityLeftSubArea(entity, areaRefs(sender), gate)
  }

  /**
   * This will let {@code entity} enter {@code area}.<br>
   * {@code area} must be created by this Manager.
   * @param entity the EntityData of the Entity that you want to let to enter the given area
   * @param area the Area the entity will enter
   * @param gate through what gate the entity will enter
   */
  protected final def enter(entity: EntityData, area: AreaRef, gate: Gate) = {
    require(areaRefs.valuesIterator contains area, "area is not created by this ManagerArea")
    area.actorRef ! EntityEnter(entity, gate)
  }

  /**
   * Creates a reference to an Area based on Area creation function.
   */
  protected final def create(createArea: => Area, name: String): AreaRef =
    create(Props(createArea.asInstanceOf[Actor]), name)

  /**
   * Creates a reference to an Area based on the class of that Area.
   */
  protected final def create[T <: Area](name: String)(implicit man: Manifest[T]): AreaRef =
    create(Props[T], name)

  private def create(props: Props, name: String): AreaRef = {
    val actorRef = context.actorOf(props, name)
    actorRef ! self
    val area = new AreaRef(actorRef)
    areaRefs += actorRef -> area
    area
  }

}

object ManagerArea {
  /**
   * A reference to an Area. This is used in the ManagerArea to tell an Area that an entity is entering.
   */
  class AreaRef(private[ManagerArea] val actorRef: ActorRef)
}