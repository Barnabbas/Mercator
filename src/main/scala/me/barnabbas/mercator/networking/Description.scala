package me.barnabbas.mercator.networking

/**
 * A Description of a ViewComponent. Will be send on the Connect message.
 * A Description is used to explain the Client what is trying to connect to it.
 * @author Barnabbas
 */
sealed trait Description

object Description{
  /**
   * A View that interacts with text
   */
  case object Text extends Description
  /**
   * A View that wants to display a side scrolling area.
   * @param identifier an identifier of the area.
   */
  case class SideScrolling3D(identifier: String) extends Description
  /**
   * A View that listens to the Controls that are pressed and sends it to the server.
   */
  case object ControlListener extends Description
}