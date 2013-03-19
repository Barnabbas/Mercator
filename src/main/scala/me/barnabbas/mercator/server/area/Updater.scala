package me.barnabbas.mercator.server.area

import akka.actor.ActorContext
import scala.concurrent.duration._

class Updater private(action: Int => Unit, duration: FiniteDuration, context: ActorContext) {
  
  private implicit val dispatcher = context.dispatcher
  
  /** the time at the last call of the scheduler */ 
  private var t0 = System.currentTimeMillis
  
  // scheduling the following function
  val cancellable = context.system.scheduler.schedule(duration, duration){
    val t1 = System.currentTimeMillis
    val Δt = t1 - t0 toInt
    
    // calling the given function
    action(Δt)
    
    t0 = t1
  }
  
  /**
   * Stops this Updater. After stop it wont call the given action anymore
   */
  private def stop() = cancellable.cancel()

}

/**
 * An Updater allows you to have a update function called regularly
 */
object Updater {

  /**
   * The default frequency for the Updaters
   */
  val DEFAULT_FREQUENCY = 120

  /**
   * Now {@code action} will be called by the given frequency.<br>
   * It will not run the first time, but will start at his second time.
   * @param action the function that will be called, it will be given the difference in time since last call
   * @param frequency how many times per seconds {@code action} has to be called
   */
  def apply(action: Int => Unit, frequency: Int = DEFAULT_FREQUENCY)(implicit areaContext: ActorContext): Unit = {
    val duration = Duration(1000 / frequency, MILLISECONDS)
    new Updater(action, duration, areaContext)
  }
}