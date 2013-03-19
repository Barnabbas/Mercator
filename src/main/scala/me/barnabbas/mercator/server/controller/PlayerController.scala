package me.barnabbas.mercator.server.controller

import me.barnabbas.mercator.networking.Input._
import me.barnabbas.mercator.server.view.ViewComponentFactory
import me.barnabbas.mercator.server.view.ViewComponent
import me.barnabbas.mercator.server.area.sidescrolling.Entity3D
import me.barnabbas.mercator.networking.Description

/**
 * This is a Controller that interacts with a user to control an entity.
 * It uses the View to listen for the keys that the user pressed.<br>
 * Currently it uses an Entity2D to communicate with, this is only temporary however.
 */
class PlayerController(entity: Entity3D) extends ViewComponent {

  import Entity3D._
  import PlayerController._

  /** The left right direction (-1 is left, 1 is right) */
  private var leftRight = 0
  /** The down up direction (-1 is down, 1 is up) */
  private var downUp = 0

  // the reactors for the keys
  private val leftKeyReactor = new KeyReactor {
    def press() = {leftRight -= 1; updateEntity}
    def release() = {leftRight += 1; updateEntity}
  }
  private val rightKeyReactor = new KeyReactor {
    def press() = {leftRight += 1; updateEntity}
    def release() = {leftRight -= 1; updateEntity}
  }
  private val upKeyReactor = new KeyReactor {
    def press() = {downUp += 1; updateEntity}
    def release() = {downUp -= 1; updateEntity}
  }
  private val downKeyReactor = new KeyReactor {
    def press() = {downUp -= 1; updateEntity}
    def release() = {downUp += 1; updateEntity}
  }

  private val keyMap = Map(Key.Left -> leftKeyReactor, Key.Right -> rightKeyReactor,
    Key.Up -> upKeyReactor, Key.Down -> downKeyReactor,
    Key.Jump -> JumpReactor)

  // events from the client
  override def clientEvent = {
    case KeyPressed(key) => keyMap.get(key) foreach (_.press())
    case KeyReleased(key) => keyMap.get(key) foreach (_.release())
  }

  /**
   * updates the entity by setting it to the right direction
   */
  private def updateEntity() = (leftRight, downUp) match {
    case (-1, -1) => entity move Direction.DownLeft
    case (0, -1) => entity move Direction.Down
    case (1, -1) => entity move Direction.DownRight
    case (-1, 0) => entity move Direction.Left
    case (0, 0) => entity stop
    case (1, 0) => entity move Direction.Right
    case (-1, 1) => entity move Direction.UpLeft
    case (0, 1) => entity move Direction.Up
    case (1, 1) => entity move Direction.UpRight
    case x => throw new IndexOutOfBoundsException(s"$x is not inside ([-1, 1], [-1, 1])")
  }

  private object JumpReactor extends PlayerController.KeyReactor {
    override def press() = entity.jump()
    override def release() = { /* no operation */ }
  }

}

object PlayerController {

  def apply() = ViewComponentFactory[Entity3D](Description.ControlListener)(new PlayerController(_))

  /**
   * A small trait use to handle key actions
   */
  private trait KeyReactor {
    def press()
    def release()
  }

}