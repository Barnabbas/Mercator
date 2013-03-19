package me.barnabbas.mercator.client.view.sidescrolling.areas

import me.barnabbas.mercator.client.networking.SideScrollingNetworker
import me.barnabbas.mercator.client.view.sidescrolling.OutsideRenderer

import org.lwjgl.opengl.GL11._

/**
 * The renderer for the welcome field
 */
class WelcomeFieldRenderer(override val networker: SideScrollingNetworker) extends OutsideRenderer {

  private val depth = 100

  override protected def renderArea() {
    def cube(corner1: (Float, Float, Float), corner2: (Float, Float, Float),
      frontColor: (Float, Float, Float), otherColor: (Float, Float, Float)) = {
      val (x1, y1, z1) = corner1
      val (x2, y2, z2) = corner2

      val (fc1, fc2, fc3) = frontColor
      glColor3f(fc1, fc2, fc3);

      glBegin(GL_QUADS)

      // front
      glVertex3f(x1, y1, z1)
      glVertex3f(x2, y1, z1)
      glVertex3f(x2, y2, z1)
      glVertex3f(x1, y2, z1)

      val (oc1, oc2, oc3) = otherColor
      glColor3f(oc1, oc2, oc3);

      // back
      glVertex3f(x2, y2, z2)
      glVertex3f(x2, y1, z2)
      glVertex3f(x1, y1, z2)
      glVertex3f(x1, y2, z2)

      glEnd()

      // the sides

      glBegin(GL_QUAD_STRIP)

      glVertex3f(x1, y1, z2)
      glVertex3f(x1, y1, z1)

      glVertex3f(x1, y2, z2)
      glVertex3f(x1, y2, z1)

      glVertex3f(x2, y2, z2)
      glVertex3f(x2, y2, z1)

      glVertex3f(x2, y1, z2)
      glVertex3f(x2, y1, z1)

      glEnd()
    }

    def house(x: Float, y: Float, z: Float) = {

      glColor3f(.5f, .3f, .3f)

      val size = 60f
      val roofRatio = .5f

      // the sides
      glBegin(GL_QUAD_STRIP)

      glVertex3f(x, y, z)
      glVertex3f(x, y, z + size)

      glVertex3f(x, y + roofRatio * size, z)
      glVertex3f(x, y + roofRatio * size, z + size)

      glVertex3f(x + .5f * size, y + size, z)
      glVertex3f(x + .5f * size, y + size, z + size)

      glVertex3f(x + size, y + roofRatio * size, z)
      glVertex3f(x + size, y + roofRatio * size, z + size)

      glVertex3f(x + size, y, z)
      glVertex3f(x + size, y, z + size)

      glEnd()

      // the front
      glBegin(GL_QUADS)

      glVertex3f(x, y, z)
      glVertex3f(x, y + roofRatio * size, z)
      glVertex3f(x + .5f * size - 10, y + roofRatio * size, z)
      glVertex3f(x + .5f * size - 10, y, z)

      glVertex3f(x + .5f * size + 10, y, z)
      glVertex3f(x + .5f * size + 10, y + roofRatio * size, z)
      glVertex3f(x + size, y + roofRatio * size, z)
      glVertex3f(x + size, y, z)

      glVertex3f(x, y + 40, z)
      glVertex3f(x, y + roofRatio * size, z)
      glVertex3f(x + size, y + roofRatio * size, z)
      glVertex3f(x + size, y + 40, z)

      glEnd()

      glBegin(GL_TRIANGLES)

      glVertex3f(x, y + roofRatio * size, z)
      glVertex3f(x + size, y + roofRatio * size, z)
      glVertex3f(x + .5f * size, y + size, z)

      glEnd()

      glColor3f(.5f, .5f, .5f)

      // the back
      glBegin(GL_QUADS)

      glVertex3f(x, y + roofRatio * size, z + size)
      glVertex3f(x + size, y + roofRatio * size, z + size)
      glVertex3f(x + size, y, z + size)
      glVertex3f(x, y, z + size)

      glEnd()

      glBegin(GL_TRIANGLES)

      glVertex3f(x, y + roofRatio * size, z + size)
      glVertex3f(x + .5f * size, y + size, z + size)
      glVertex3f(x + size, y + roofRatio * size, z + size)

      glEnd()

    }

    // the ground
    glColor3f(0.3f, .7f, 0.3f);
    glBegin(GL_QUADS);
    glVertex3f(-100, 150, 0);
    glVertex3f(900, 150, 0);
    glVertex3f(900, 150, depth);
    glVertex3f(-100, 150, depth);
    glEnd();

    // small path to the house
    glBegin(GL_QUADS)
    glVertex3f(270, 150, depth)
    glVertex3f(270, 150, depth + 20)
    glVertex3f(270 + 20, 150, depth + 20)
    glVertex3f(270 + 20, 150, depth)

    // the obstacle
    cube((150, 150, 0), (200, 190, depth), (0f, 0f, 0f), (0.3f, .7f, 0.3f))

    house(250, 150, depth + 20)
  }
}