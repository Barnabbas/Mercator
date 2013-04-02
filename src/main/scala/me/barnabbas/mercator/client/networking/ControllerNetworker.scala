package me.barnabbas.mercator.client.networking

import org.lwjgl.input.Keyboard._
import me.barnabbas.mercator.networking.Input._
import me.barnabbas.mercator.networking.Description
import me.barnabbas.mercator.client.view.ControllerListener
import me.barnabbas.mercator.networking.Entity

class ControllerNetworker(parent: SideScrollingNetworker) extends NetworkerComponent {
  
  import ControllerNetworker._
  
  /**
   * The actions that should be done when a key is pressed.
   * The first is the action that has to be done when the key is pressed.
   * The second is the action for when the key is released.
   * 
   */
  private val actions = Map(
      // the keys that are forwarded
      KEY_LEFT -> forward(Key.Left), KEY_RIGHT -> forward(Key.Right),
      KEY_UP -> forward(Key.Up), KEY_DOWN -> forward(Key.Down), KEY_SPACE -> forward(Key.Jump),
      
      // the other keys
      KEY_TAB -> (select _, NOP),
      KEY_LCONTROL -> (interact _, NOP))

  /** not used */
  override def receiveMessage = {
    case _ => sys.error("Not expecting a message")
  }
  
  def pressed(key: Int) = actions(key)._1()
  
  def released(key: Int) = actions(key)._2()
  
  override def renderer(des: Description) = new ControllerListener(this)
  
  
  // Actions for the actions Map
  
  /**
   * Sends the key to the server
   */
  def forward(key: Key) = (() => server ! KeyPressed(key), () => server ! KeyReleased(key))
  
  /**
   * Selects a new entity
   */
  def select() = {
    def p(entity: Entity) = {
      !(entity ~= parent.player) &&
      !(entity ~= parent.selected)
    }
    val select = parent.entities flatMap (_.find(p))
    parent.selected = select
  }
  
  /**
   * Sends an interact message for the current selected entity
   */
  def interact() = for (entity <- parent.selected){
    server ! Interact(entity.id)
  }
  
  /** no operation value */
  def NOP = () => {}
  
}

private object ControllerNetworker {
  implicit class RichEntity(entity: Entity){
    def ~= (other: Entity) = entity.id == other.id
    def ~= (option: Option[Entity]) = option map (entity.id == _.id) getOrElse false
  }
}