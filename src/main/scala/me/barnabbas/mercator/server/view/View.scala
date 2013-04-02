package me.barnabbas.mercator.server.view

import akka.actor.Actor
import akka.actor.ActorContext
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.TypedActor
import akka.pattern.ask
import me.barnabbas.mercator.networking.Messages._
import akka.actor.TypedProps
import me.barnabbas.mercator.server._
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import akka.dispatch.ExecutionContexts

/**
 * A View is a the component of an Entity that shows what the Entity is viewing and tells what the Entity is doing.
 * A View is defined through Viewers. Those Viewers are components that manages a small part of a View.
 * @author Barnabbas
 */
trait View[E] {

  /**
   * Connects this View with all of its Components to the given Client.
   * Currently we only support one Client, so this method may only be called once.
   */
  def connect(client: Client)

  /**
   * Disconnects this View from the Client this View is connected to.
   * @return when it is done
   */
  def disconnect(): Future[Unit]
}

object View {

  // the implicit vals used for this View
  private implicit val timeout = Timeout(10, TimeUnit.SECONDS)
  private implicit val executionContext = ExecutionContexts.global()

  /**
   * Creates a new View for {@code entity}. Based on the given components.
   * @param E the type of the Entity (can differ for each Map)
   * @param components the factories for the ViewComponents
   * @param entity the Entity that will be watched
   */
  def apply[E](components: Seq[ViewComponentFactory[E]], entity: E)(implicit areaContext: ActorContext) = {
    // creating a typed actor of View
    val props = TypedProps(classOf[View[E]], new ViewImpl(components, entity))
    TypedActor(areaContext) typedActorOf props
  }

  /**
   * The implementation for the View
   */
  private class ViewImpl[E](componentFactories: Seq[ViewComponentFactory[E]], entity: E) extends View[E] {

    /** The ViewComponents of this View */
    private val components = for (factory <- componentFactories) yield {
      TypedActor.context actorOf (Props(factory(entity)))
    }

    /** The Client this View is connected to */
    private var client = Option.empty[Client]

    def connect(client: Client) = {
      require(this.client.isEmpty, "You can only connect to one Client")
      this.client = Some(client)
      for ((comp, factory) <- components zip componentFactories) {
        client ! Connect(comp, factory.description)
      }
    }

    
    def disconnect(): Future[Unit] = Future.traverse(components) { actor =>
      actor ? Disconnect
    } map (a => {})

  }

}