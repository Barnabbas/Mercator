package me.barnabbas.mercator.server.area.sidescrolling

import scala.math.ScalaNumericAnyConversions
import Point._

private[sidescrolling] trait Point[P <: Point[P]] {
  
  /** The number of elements in this Point */
  protected val arity: Int
  
  /** Gets the n^th^ element of this Point */
  def element(n: Int): Float
  
  /**
   * Factory function for this Point type
   */
  protected def create(floats: Seq[Float]): P
  
  /** the range used for the elements of this Point */
  private lazy val range = 0 until arity
  
  /**
   * Calculates a new Point, using a calculation for each value where an other Point is also used.<br>
   * For example for {@code x} we will get {@code x = f(this.x, other.x)}.
   * @param other an other Point to use in the calculation
   * @param f a function between a value of this Point, one of other resulting in the new value
   */
  def map(other: P)(f: (Float, Float) => Float) = {
    val values = for (i <- range) yield f(element(i), other.element(i))
    create(values)
  }
  
  override def equals(arg: Any) = {
    arg.isInstanceOf[Point[P]] && {
      val p = arg.asInstanceOf[Point[P]]
      range forall (i => element(i) == p.element(i))
    }
  }
  
  override def hashCode = (range map (element _)).hashCode + 13
  
  override def toString = range map (element(_)) mkString("(", ", ", ")")
  
}

/**
 * A two dimensional point
 */
final class Point2D(val x: Float, val y: Float) extends Point[Point2D] {
  
  protected val arity = 2
  def element(i: Int) = i match {
    case 0 => x
    case 1 => y
  }
  protected def create(floats: Seq[Float]) = {
    require(floats.length == arity, s"length should be $arity not ${floats.length}")
    new Point2D(floats(0), floats(1))
  }

  /**
   * Makes a copy of this Point
   */
  def copy(x: Num = this.x, y: Num = this.y) = Point2D(x, y)

  /**
   * Maps this Point to a tuple of the given type.
   */
  def map[T](f: Float => T) = (f(x), f(y))
  
  /**
   * Gives a tuple with all values cast to ints
   */
  def toInts = map[Int](_ toInt)

}

/**
 * A two dimensional point
 */
final class Point3D(val x: Float, val y: Float, val z: Float) extends Point[Point3D] {
  
  protected val arity = 3
  def element(i: Int) = i match {
    case 0 => x
    case 1 => y
    case 2 => z
  }
  protected def create(floats: Seq[Float]) = {
    require(floats.length == arity, s"length should be $arity not ${floats.length}")
    new Point3D(floats(0), floats(1), floats(2))
  }
  
  /**
   * Makes a copy of this Point
   */
  def copy(x: Num = this.x, y: Num = this.y, z: Num = this.z) = Point3D(x, y, z)

  /**
   * Maps this Point to a tuple of the given type.
   */
  def map[T](f: Float => T) = (f(x), f(y), f(z))
  
  /**
   * Gives a tuple with all values cast to ints
   */
  def toInts = map[Int](_ toInt)
}

object Point {
  type Num = ScalaNumericAnyConversions
  implicit def Point2D(tuple: (Num, Num)): Point2D = {
    val (x, y) = tuple
    new Point2D(x.toFloat, y.toFloat)
  }
  implicit def Point3D(tuple: (Num, Num, Num)): Point3D = {
    val (x, y, z) = tuple
    new Point3D(x.toFloat, y.toFloat, z.toFloat)
  }

  def Point(tuple: (Num, Num)) = Point2D(tuple)
  def Point(tuple: (Num, Num, Num)) = Point3D(tuple)

}