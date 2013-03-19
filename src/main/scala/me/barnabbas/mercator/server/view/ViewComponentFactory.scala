package me.barnabbas.mercator.server.view

import me.barnabbas.mercator.networking.Description

/**
 * A Factory that creates ViewComponents.
 *
 */
trait ViewComponentFactory[E] {

  /**
   * A description of the Components this Factory will create
   */
  val description: Description

  /**
   * Creates a new ViewComponent for {@code entity}
   */
  def apply(entity: E): ViewComponent
}

object ViewComponentFactory {
  /**
   * Creates a ViewComponentFactory that uses the given ViewComponent function
   */
  def apply[E](desc: Description)(factory: E => ViewComponent) =
    new ViewComponentFactory[E] {
      override val description = desc
      override def apply(entity: E) = factory(entity)
    }

}