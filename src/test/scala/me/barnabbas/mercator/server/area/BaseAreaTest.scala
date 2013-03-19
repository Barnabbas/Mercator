package me.barnabbas.mercator.server.area

import org.scalatest.FunSpec
import org.scalatest.FeatureSpec
import org.scalatest.FunSuite
import akka.testkit.TestActorRef
import me.barnabbas.mercator.server.EntityData
import org.scalatest.GivenWhenThen
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import org.scalatest.BeforeAndAfterAll

class BaseAreaTest extends FunSuite with GivenWhenThen with BeforeAndAfterAll {

  import BaseAreaTest._

  implicit val system = ActorSystem("BaseAreaTest")

  val actorRef = TestActorRef[TestBaseArea]
  val managerRef = TestActorRef[TestManager]

  managerRef ! ("START", actorRef)

  test("Letting an Entity enter") {
    Given(s"An entity")
    val (entity, gate) = (new EntityData("test", null), Gate.LogIn)

    When("the entities enters")
    managerRef ! ("SEND", AreaMessage.EntityEnter(entity, gate))

    Then("onEntityEnter should have been called")
    assert(actorRef.underlyingActor.data === (entity, gate))
  }

  test("leaveEntity") {
    Given("An entity")
    val data = (new EntityData("test2", null), Gate.LogOut)

    When("leaveEntity is called")
    actorRef.underlyingActor.testLeave(data)

    Then("The BaseArea should inform its manager")
    assert(managerRef.underlyingActor.receivedData === AreaMessage.EntityLeave(data._1, data._2))
  }

  override def afterAll() = {
    system.shutdown()
  }

}

object BaseAreaTest {
  class TestManager extends Actor {

    var receivedData: Any = _
    var baseArea: ActorRef = _

    override def receive = {
      case ("START", actor: ActorRef) => {
        baseArea = actor
        baseArea ! self
      }
      case ("SEND", data) => baseArea ! data
      case data => receivedData = data
    }
  }

  class TestBaseArea extends BaseArea {

    var data: (EntityData, Gate) = _

    override def onEntityEnter(entity: EntityData, gate: Gate) = {
      data = (entity, gate)
    }

    /** An auxilary function to enable the super.leaveEntity function */
    def testLeave(data: (EntityData, Gate)) = super.leaveEntity(data._1, data._2)
  }
}