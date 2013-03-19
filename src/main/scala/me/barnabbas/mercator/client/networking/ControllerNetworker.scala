package me.barnabbas.mercator.client.networking

import org.lwjgl.input.Keyboard._
import me.barnabbas.mercator.networking.Input._
import me.barnabbas.mercator.networking.Description
import me.barnabbas.mercator.client.view.ControllerListener

class ControllerNetworker extends NetworkerComponent {
  
  val keyMap = Map(KEY_LEFT -> Key.Left, KEY_RIGHT -> Key.Right,
      KEY_UP -> Key.Up, KEY_DOWN -> Key.Down, KEY_SPACE -> Key.Jump)

  /** not used */
  override def receiveMessage = {
    case _ => sys.error("Not expecting a message")
  }
  
  def pressed(key: Int) = server ! KeyPressed(keyMap(key))
  
  def released(key: Int) = server ! KeyReleased(keyMap(key))
  
  override def renderer(des: Description) = new ControllerListener(this)
  
}