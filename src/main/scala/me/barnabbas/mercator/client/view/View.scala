package me.barnabbas.mercator.client.view

import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode
import org.lwjgl.opengl.GL11._
import scala.collection.mutable.SynchronizedBuffer
import scala.collection.mutable.UnrolledBuffer
import scala.collection.mutable.Buffer
import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import me.barnabbas.mercator.client.Application
import org.lwjgl.BufferUtils
import org.lwjgl.util.glu.GLU
import scala.collection.mutable.HashSet
import scala.collection.mutable.SynchronizedSet

/**
 * Creates and destroys the LWJGL context and make sure the renderers are rendered.
 */
object View {

  /**
   * The renderers that this View will render
   */
  private var renderers = Set.empty[Renderer]

  private val FPS = 120

  val actorSystem = ActorSystem("View")

  def start() = {
    actorSystem.actorOf(Props[ViewActor], "ViewActor")
  }

  /**
   * Adds {@code renderer} to the View, such that it also will be rendered.
   */
  def +=(renderer: Renderer) = synchronized {
    renderers += renderer
  }

  /**
   * Removes {@code renderer} from the View again.
   */
  def -=(renderer: Renderer) = synchronized {
    renderers -= renderer
  }

  def exit() = actorSystem.shutdown()

  /**
   * Runs this View
   */
  private def run() = {

    // Init 
    Display.setDisplayMode(new DisplayMode(800, 600));
    Display.setTitle("Mercator")
    Display.create();

    // enabling settings
    glEnable(GL_DEPTH_TEST);
    glViewport(0, 0, 800, 600);
    glMatrixMode(GL_PROJECTION);
    
    // main loop
    while (!Display.isCloseRequested()) {

      // Clear the screen and depth buffer
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

      // running the renderers
      synchronized { for (render <- renderers) render() }

      // updating + waiting for the display
      Display.update();
      Display.sync(FPS);
    }

    Display.destroy();
    Application.shutdown()

  }

  /**
   * A small actor that calls our View functions. Such that we can start this asynchronous
   */
  private class ViewActor extends Actor {
    override def preStart() = run()
    override def receive = { case _ => ??? }
  }

}