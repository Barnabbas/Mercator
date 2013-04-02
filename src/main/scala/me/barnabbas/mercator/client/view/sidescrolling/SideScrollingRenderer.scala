package me.barnabbas.mercator.client.view.sidescrolling

import me.barnabbas.mercator.client.view.Renderer
import me.barnabbas.mercator.client.networking.SideScrollingNetworker
import me.barnabbas.mercator.networking.AreaSet
import me.barnabbas.mercator.networking.Entity

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

    networker.entities foreach renderEntities
    networker.selected foreach renderSelected
    networker.update()
  }

  /**
   * Renders the entities of this SideScrolling map, based on the AreaSet
   */
  protected def renderEntities(entities: Iterable[Entity]) {
    entities foreach renderEntity
  }

  /**
   * Renders one entity.
   */
  protected def renderEntity(entity: Entity) {
    val (x, y, z) = entity.location
    val (width, height) = (16, 32)

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
  
  protected def renderSelected(entity: Entity) {
    
    val (x, y, z) = entity.location
    val width = 16
    
    // data for the circle
    val (cx, cy, cz) = (x + width / 2, y + 1, z + width / 2)
    val radius = width * 1.2
    
    // the color of the circle 
    glColor3f(.7f, .7f, .7f)
    
    glBegin(GL_TRIANGLE_FAN)
    glVertex3f(cx, cy, cz)
    
    for(t <- .0 to (2 * math.Pi) by math.Pi / 8){
      val vx = (cx + math.sin(t) * radius).toFloat
      val vz = (cz + math.cos(t) * radius).toFloat
      glVertex3f(vx, cy, vz)
    }
    
    glEnd()
      
    
  }

}