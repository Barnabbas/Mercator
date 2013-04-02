package me.barnabbas.mercator.client.networking

import me.barnabbas.mercator.networking.AreaSet
import me.barnabbas.mercator.networking.Messages.Update
import me.barnabbas.mercator.networking.Description
import me.barnabbas.mercator.networking.Description.SideScrolling3D
import me.barnabbas.mercator.client.view.sidescrolling.areas._
import akka.actor.ActorRef
import me.barnabbas.mercator.networking.Messages.Connect
import akka.actor.Props
import me.barnabbas.mercator.networking.Entity

class SideScrollingNetworker extends ParentComponent {

  /** the areaSet received from the server (updated each time) */
  private var _areaSet = Option.empty[AreaSet]

  /** the id of the selected entity */
  //  private var _selected = Option.empty[Any]
  private var _selected = Option.empty[Any]

  override def receiveMessage = {
    case areaSet: AreaSet => {
      _areaSet = Some(areaSet)
    }
    case x => println(s"SideScrollingNetworker: receiving $x")
  }

  override def renderer(description: Description) = {
    val SideScrolling3D(identifier) = description
    identifier match {
      case "welcome.field" => new WelcomeFieldRenderer(this)
      case "welcome.house" => new WelcomeHouseRenderer(this)
    }
  }

  override def connectComponent(connect: Connect) {
    // we only accept ControlListeners
    val Connect(actor, Description.ControlListener) = connect
    val controller = context.actorOf(Props(new ControllerNetworker(this)), "controller")
    controller ! NetworkerComponent.Start(connect)
  }

  /**
   * Makes this networker update.<br>
   * Note this is not directly, it uses the server to update.
   */
  def update() = server ! Update

  /**
   * The entities of this area
   */
  def entities = _areaSet map (_.entities)

  /**
   * The player of this area, if there is one.
   */
  def player = _areaSet flatMap (_.player flatMap entity)

  /**
   * The entity that is selected by this SideScrollingNetworker.
   */
  def selected = _selected flatMap (entity _)

  /**
   * Sets the selected entity
   */
  def selected_=(entity: Option[Entity]) = _selected = entity map (_.id)

  /**
   * The entity with the given id (if there is any)
   */
  private def entity(id: Any) = {
    _areaSet flatMap (_.entities find (_.id == id))
  }
}