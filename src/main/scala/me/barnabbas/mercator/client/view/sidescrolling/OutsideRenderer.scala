package me.barnabbas.mercator.client.view.sidescrolling

import org.lwjgl.opengl.GL11._

/**
 * Renderer intended to Renderer sidescrolling areas in the outside, like in the field
 */
trait OutsideRenderer extends SideScrollingRenderer {

  protected override def setView() {
    glEnable(GL_DEPTH_TEST);
    
    glMatrixMode(GL_PROJECTION)

    glLoadIdentity();

    // setting start position
    glOrtho(0, 800, 0, 600, 400, -800);
    
    // rotating the map a little
    glRotatef(-15, 1, 0, 0)
    glRotatef(10, 0, 1, 0)
    
    glMatrixMode(GL_MODELVIEW)
    
  }
}