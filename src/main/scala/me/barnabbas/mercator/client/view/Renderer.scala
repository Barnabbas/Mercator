package me.barnabbas.mercator.client.view

/**
 * Runs LWJGL code that must be runned on the render thread.
 */
trait Renderer {
  /**
   * Renders this Renderer
   */
  def apply()
}