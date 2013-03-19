package me.barnabbas.mercator.server.view

import org.scalatest.FunSuite
import akka.actor.Actor
import me.barnabbas.mercator.networking.Messages._
import akka.actor.ActorRef
import org.scalatest.BeforeAndAfter
import akka.actor.ActorSystem
import me.barnabbas.mercator.networking.Description
import akka.testkit.TestActorRef

class ViewComponentActorTest extends FunSuite with BeforeAndAfter  {
  
  import ViewComponentActorTest._
  
  var client: ActorRef = _
  var component: ActorRef = _
  var system: ActorSystem = _
  
  before{
    system = ActorSystem("testViewComponent")
    implicit val actorSystem = system
    component = TestActorRef(new ViewComponentActor(new TestComponent))
    client = TestActorRef[TestClient]
    
    // connecting
    client ! Connect(component, Description.ControlListener)
  }
  
  after {
    system.shutdown()
  }
  
  
  test("Connecting a ViewComponent"){
    assert(lastMessage === "preRunning", "Prerunning should be called on connecting")
  }
  
  test("Update"){
    client ! ("send", Update)
    assert(lastMessage === "update", "Update is not called after an Update message has been send")
  }
  
  test("ClientEvent"){
    client ! ("send", "something")
    assert(lastMessage === ("clientEvent", "something"))
  }
  
  test("ServerEvent"){
    component ! "somethingElse"
    assert(lastMessage === ("serverEvent", "somethingElse"))
  }
  
  test("Disconnect"){
    component ! Disconnect
    assert(lastMessage === Disconnect)
  }

}

object ViewComponentActorTest {
  
  /** the last message send to the client */
  var lastMessage: Any = null
  
  /**
   * A test ViewComponent which sends a message back to the client each 
   */
  class TestComponent extends ViewComponent{
    override def preRunning() = client ! "preRunning"
    
    override def clientEvent() = {
      case message => client ! ("clientEvent", message)
    }
    
    override def serverEvent() = {
      case message => client ! ("serverEvent", message)
    }
    
    override def update() = client ! "update"
  }
  
  /** A TestClient, simulating the behavior of a client */
  class TestClient extends Actor {
    var component: ActorRef = _
    override def receive = {
      case Connect(actor, _) => {
        component = actor
        component ! self
      }
      case ("send",  message) => component ! message
      case message => lastMessage = message
    }
  }
}