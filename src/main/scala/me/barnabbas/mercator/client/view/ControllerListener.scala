package me.barnabbas.mercator.client.view

import org.lwjgl.input.Keyboard._
import akka.actor.ActorRef
import me.barnabbas.mercator.client.networking.ControllerNetworker

class ControllerListener(networker: ControllerNetworker) extends Renderer {
  
  // todo: dont use polling, but use the eventbuffer
  
  // the keys that are pressed and released
  // the keys are made such that releasedKeys ++ pressedKeys == keys
  private val keys = Set(KEY_LEFT, KEY_RIGHT, KEY_UP, KEY_DOWN, KEY_SPACE)
  private var releasedKeys = keys
  private var pressedKeys = Set.empty[Int]
  
  def apply() = {
    
    // getting changed keys
    val justPressed = for (key <- releasedKeys if isKeyDown(key)) yield key
    val justReleased = for (key <- pressedKeys if !isKeyDown(key)) yield key
    
    // updating values
    releasedKeys = (releasedKeys ++ justReleased) -- justPressed
    pressedKeys = (pressedKeys ++ justPressed) -- justReleased
    
    // updating networker
    for (key <- justPressed) networker.pressed(key)
    for (key <- justReleased) networker.released(key)
    
  }

}