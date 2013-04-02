package me.barnabbas.mercator.server.view

import me.barnabbas.mercator.networking.Description
import akka.actor.Actor

/**
 * A Factory that creates ViewComponents.
 *
 */
trait ViewComponentFactory[E] extends (E => ViewComponentActor) {

  /**
   * A description of the Components this Factory will create
   */
  val description: Description

  /**
   * Creates a new ViewComponent for {@code entity}
   */
  def apply(entity: E): ViewComponentActor
  
  override def toString = description.toString
}

object ViewComponentFactory {
  /**
   * Creates a ViewComponentFactory that uses the given ViewComponent function
   */
  def apply[E](desc: Description)(factory: E => ViewComponentActor) =
    new ViewComponentFactory[E] {
      override val description = desc
      override def apply(entity: E) = factory(entity)
    }

}