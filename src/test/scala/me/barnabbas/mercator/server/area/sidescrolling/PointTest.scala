package me.barnabbas.mercator.server.area.sidescrolling

import org.scalatest.GivenWhenThen
import org.scalatest.FunSuite

class Point2DTest extends FunSuite with GivenWhenThen {

  import Point._

  Given("Point2D")
  val point = Point2D(1, 0)

  test("create") {
    val point = Point2D(1, 2.3)
    assert(point.x === 1f)
    assert(point.y === 2.3f)
  }

  test("copy") {
    val p1 = point.copy()
    assert(point === p1)

    val p2 = point.copy(x = .3f)
    assert(p2 === Point2D(.3f, 0))

    val p3 = point.copy(y = 100)
    assert(p3 === Point2D(1, 100))

    val p4 = point.copy(x = .1, y = -1)
    assert(p4 === Point2D(.1, -1))
  }

  test("map") {
    val p1 = point map (_ + 1 toInt)
    assert(p1 === (2, 1))

    val p2 = point.map(Point2D(1, 5)) { (f1, f2) => f1 + f2 }
    assert(p2 === Point2D(2, 5))
  }

}

class Point3DTest extends FunSuite with GivenWhenThen {

  import Point._

  Given("Point3D")
  val point = Point3D(1, 0, -1.5)

  test("create") {
    val point = Point3D(1, 2.3, 100l)
    assert(point.x === 1f)
    assert(point.y === 2.3f)
    assert(point.z === 100f)
  }

  test("copy") {
    val p1 = point.copy()
    assert(point === p1)

    val p2 = point.copy(x = .3f)
    assert(p2 === Point3D(.3f, 0, -1.5))

    val p3 = point.copy(y = 100)
    assert(p3 === Point3D(1, 100, -1.5))

    val p4 = point.copy(x = .1, y = -1)
    assert(p4 === Point3D(.1, -1, -1.5))

    val p5 = point.copy(z = 20)
    assert(p5 === Point(1, 0, 20))

    val p6 = point.copy(x = 100l, y = -2.6, z = 20)
    assert(p6 === Point(100l, -2.6, 20))
  }

  test("map") {
    val p1 = point map (_ + 1 toInt)
    assert(p1 === (2, 1, 0))

    val p2 = point.map(Point3D(1, 5, 3)) { (f1, f2) => f1 + f2 }
    assert(p2 === Point3D(2, 5, 1.5))
  }

}