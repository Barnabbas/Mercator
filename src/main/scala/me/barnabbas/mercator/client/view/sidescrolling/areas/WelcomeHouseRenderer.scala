package me.barnabbas.mercator.client.view.sidescrolling.areas

import me.barnabbas.mercator.client.view.sidescrolling.InsideRenderer
import me.barnabbas.mercator.client.networking.SideScrollingNetworker

import org.lwjgl.opengl.GL11._

/**
 * The renderer for the first building
 */
class WelcomeHouseRenderer(override val networker: SideScrollingNetworker) extends InsideRenderer {
  
  private val (width, height, depth) = (300, 150, 100)
  private val buildingHeight = 230
  
  override protected def renderArea(){
    
    /** the house from the inside */
    def box() = {
      
      // inside
      glColor3f(.5f, .3f, .1f)
      
      glBegin(GL_QUAD_STRIP)
      glVertex3f(0, height, 0)
      glVertex3f(0, buildingHeight, 0)
      glVertex3f(0, height, depth)
      glVertex3f(0, buildingHeight, depth)
      glVertex3f(width, height, depth)
      glVertex3f(width, buildingHeight, depth)
      glEnd()
      
      // outside
      glColor3f(.5f, .3f, .3f)
      
      glBegin(GL_QUAD_STRIP)
      glVertex3f(width, height, depth)
      glVertex3f(width, buildingHeight, depth)
      glVertex3f(width, height, 0)
      glVertex3f(width, buildingHeight, 0)
      glEnd()
      
      glBegin(GL_QUAD_STRIP)
      glVertex3f(0, buildingHeight, 0)
      glVertex3f(0, buildingHeight, depth)
      glVertex3f(width, buildingHeight, 0)
      glVertex3f(width, buildingHeight, depth)
      glEnd()
    }
    
    // the floor
    glColor3f(0.8f, .5f, 0.1f);
    glBegin(GL_QUADS);
    glVertex3f(0, height, 0);
    glVertex3f(width, height, 0);
    glVertex3f(width, height, depth);
    glVertex3f(0, height, depth);
    glEnd();
    
    // the exit
    glBegin(GL_QUADS)
    glVertex3f(130, height, 0)
    glVertex3f(170, height, 0)
    glVertex3f(170, height, -20)
    glVertex3f(130, height, -20)
    glEnd()
    
    box()
  }

}