package me.barnabbas.mercator.server.view

import me.barnabbas.mercator.networking.Description
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import me.barnabbas.mercator.networking.Messages._
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import akka.dispatch.ExecutionContexts

/**
 * A ViewComponentParent is a ViewComponent that has children.
 * Using children for a ViewComponent allows ViewComponents to work together.
 */
trait ViewComponentParent extends ViewComponent {

  /** generates the children for this parent, together with the descriptions */
  protected def childrenFactories: Seq[(() => ViewComponentActor, Description)]

}

// name is waaay to long :p
/**
 * A ViewComponentActor that makes sure that the children got connected.
 */
trait ViewComponentParentActor extends ViewComponentActor with ViewComponentParent {

  /** the children actors of this ViewComponent, together with their descriptions */
  private var children: Seq[(ActorRef, Description)] = Seq.empty

  /** The timeout, used for the ask function */
  private implicit val timeout = Timeout(10, TimeUnit.SECONDS)
  /** some outher implicit val that is required */
  private implicit val executionContext = ExecutionContexts.global()

  /**
   * Creating the children
   */
  override def preStart() {
    super.preStart()

    children = for ((creator, description) <- childrenFactories) yield {
      val actor = context.actorOf(Props(creator))
      (actor, description)
    }
  }

  /**
   * handling the initial connect message from the client
   */
  override def receive = {
    case actor: ActorRef => {
      super.receive(actor)

      connectChildren()
    }

    case x => super.receive(x)
  }

  /**
   * The receive method for when we are already running
   */
  protected override def running: Receive = {
    case Disconnect => { // stopping this component

      val parent = sender

      // disconnecting children
      val future = Future.traverse(children) { childDes =>
        childDes._1 ? Disconnect
      }

      // disconnecting our self
      future foreach { _ =>
        client ! Disconnect
        parent ! "ACK"
        context stop self
      }
    }
    case x => super.running(x)
  }

  /**
   * Connects the children (and adds them to the list)
   */
  private def connectChildren() {
    for ((actor, description) <- children) {
      client ! Connect(actor, description)
    }
  }

}