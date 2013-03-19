package me.barnabbas.mercator.client.view.sidescrolling

import org.lwjgl.opengl.GL11._

/**
 * Renders the inside of a building.<br>
 * This uses a view that is more zoomed-in.
 */
trait InsideRenderer extends SideScrollingRenderer {

  protected override def setView() {
    glEnable(GL_DEPTH_TEST);
    
    glMatrixMode(GL_PROJECTION)

    glLoadIdentity();

    // setting start position
    glOrtho(0, 800, 0, 600, 400, -800);
    
    // rotating the map a little
    glRotatef(-15, 1, 0, 0)
    glRotatef(5, 0, 1, 0)
    
    glScalef(2, 2, 2)
    glTranslatef(50, -100, 0) 
    
    glMatrixMode(GL_MODELVIEW)
    
    
    
  }
}