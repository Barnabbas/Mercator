package me.barnabbas.mercator.client.view.sidescrolling

import me.barnabbas.mercator.client.view.Renderer
import me.barnabbas.mercator.client.networking.SideScrollingNetworker
import me.barnabbas.mercator.networking.AreaSet

import org.lwjgl.opengl.GL11._

trait SideScrollingRenderer extends Renderer {

  /**
   * The networker that created this Renderer.
   */
  protected val networker: SideScrollingNetworker

  /**
   * Sets the camera at the right position for this Renderer
   */
  protected def setView()

  /**
   * Renders the area for the View.
   * Like the map and the background, but not the characters.
   */
  protected def renderArea()

  override def apply() {
    setView()
    renderArea()

    networker.areaSet foreach (renderEntities _)
    networker.update()
  }

  /**
   * Renders the entities of this SideScrolling map, based on the AreaSet
   */
  protected def renderEntities(areaSet: AreaSet) {
    for (player <- areaSet.player) {
      val (x, y, z) = player.location
      val (width, height) = (16, 32)

      // running the given code
      // set the color of the quad (R,G,B,A)
      glColor3f(0.5f, 0.5f, 1.0f)

      // draw quad
      glBegin(GL_QUADS)
      glVertex3f(x, y, z + 8)
      glVertex3f(x + width, y, z + 8)
      glVertex3f(x + width, y + height, z + 8)
      glVertex3f(x, y + height, z + 8)
      glEnd()
    }
  }

}