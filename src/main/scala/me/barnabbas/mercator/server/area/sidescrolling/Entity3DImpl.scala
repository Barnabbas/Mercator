package me.barnabbas.mercator.server.area.sidescrolling

import me.barnabbas.mercator.server.EntityData
import Entity3D._
import Entity3DImpl._
import akka.actor.TypedActor

/**
 * The implementation of Entity3D. (required to use it for the typedActors.)
 */
private[sidescrolling] class Entity3DImpl(entityData: EntityData, area: SideScrollingArea, loc: Point3D)
	extends Entity3D {

  // giving access to Tuple2 to Point2D conversions
  import Point._
  import Entity3DImpl._

  private val size = Point3D(16, 32, 16)
  private var _loc: Point3D = loc
  private var _speed: Point3D = Point3D(0, 0, 0)
  
  
  /**
   * The status of this Entity
   */
  private var status = Status.Stopped
  /**
   * The direction of this Entity (what direction will we be moving into)
   */
  private var direction: Direction = Direction.Right
  
  /**
   * If the player is standing on the floor.
   * Will constantly be updated by the update function.
   */
  private var onTheFloor: Boolean = _

  /**
   * The location of this Entity
   */
  def location = _loc

  /**
   * Updates this Entity2D
   * @param the time since last update
   */
  def update(time: Int, tileMap: TileMap) = {

    onTheFloor = tileMap.isOnFloor(location, size)

    val fac = time / 1000f

    // updating speed
    _speed = _speed.map(acceleration)((a, b) => a + b * fac)
    
    val goal = _loc.map(_speed)((a, b) => a + b * fac)
    
    val TileMap.MoveResponse(loc, collisions) = tileMap.move(location, size, TypedActor.self)(goal)
    
    // updating loc
    _loc = loc
    
    // collisions events
    import TileMap.Collision._
    for (col <- collisions) col match {
      case Wall => hitWall()
      case Floor => hitFloor()
      case Ceiling => hitCeiling()
      case Edge => hitEdge()
    }
  }

  import Direction._

  /**
   * Makes this Entity move into {@code direction}.
   */
  def move(direction: Direction) = {
    this.direction = direction
    this.status = Status.Walking
  }

  /**
   * Makes this Entity stop moving.
   */
  def stop() = {
    this.status = Status.Stopped
  }

  /* Some values for jumping */
  val baseSpeed = 200
  val horizontalSpeedFactor = .5f

  /**
   * Makes this character jump (currently you can still jump mid-air :/)
   */
  def jump() = {
    // we only jump when there is an obstacle below
    if (onTheFloor) {
      val speed = baseSpeed + _speed.x.abs * horizontalSpeedFactor
      _speed = _speed.copy(y = speed)
    }
  }

  /**
   * This is called when the player hits a floor vertically.
   */
  private def hitFloor() = {
    _speed = _speed.copy(y = 0)
  }

  /**
   * This is called when the player hits a ceiling vertically.
   */
  private def hitCeiling() = {
    _speed = _speed.copy(y = 0)
  }

  /**
   * This is called when the player hits a wall horizontally
   */
  private def hitWall() = {
    _speed = _speed.copy(x = 0)
  }

  /**
   * This is called when the player hits the edge of the path in the z-direction
   */
  private def hitEdge() = {
    _speed = _speed.copy(z = 0)
  }

  /**
   * Calculates the current acceleration.
   */
  private def acceleration: Point3D = Acceleration()

  /**
   * Code and values for calculating the Acceleration
   */
  private object Acceleration {

    import Direction._

    /** the fall acceleration */
    private val fallingMax = -200
    private val fallingRate = 2f

    /* Values used for walking */
    /** what the sign should be of the acceleration for the directions */
    private val walkingSign =
      Map[Direction, (Float, Float)](Left -> (-1, 0), Right -> (1, 0), Up -> (0, 1), Down -> (0, -1),
        UpLeft -> (-.7f, .7f), UpRight -> (.7f, .7f),
        DownRight -> (.7f, -.7f), DownLeft -> (-.7f, -.7f))
    private val walkingMax = 100f
    private val walkingAirRate = .7f
    private val walkingFloorRate = 1.5f

    /* Values for stopping */
    private val stoppingAirRate = 1f
    private val stoppingFloorRate = 5f

    /**
     * The acceleration for walking
     */
    private def walking = {
      val (signX, signZ) = walkingSign(direction)
      if (onTheFloor) { // on the floor
        Point3D(calc(_speed.x, signX * walkingMax, walkingFloorRate), 0,
          calc(_speed.z, signZ * walkingMax, walkingFloorRate))
      } else { // in the air
        Point3D(calc(_speed.x, signX * walkingMax, walkingAirRate), falling,
          calc(_speed.z, signZ * walkingMax, walkingFloorRate))
      }
    }

    /**
     * The acceleration for when we stopped (mostly breaking)
     */
    private def stopped = {
      if (_speed.x == 0) { // standing still already (happens often)
        Point3D(0, falling, 0)
      } else if (onTheFloor) { // we are on the floor
        Point3D(calc(_speed.x, 0, stoppingFloorRate), 0,
          calc(_speed.z, 0, stoppingFloorRate))
      } else { // in the air
        Point3D(calc(_speed.x, 0, stoppingAirRate), falling,
          calc(_speed.z, 0, stoppingFloorRate))
      }
    }

    def apply() = status match {
      case Status.Walking => walking
      case Status.Stopped => stopped
    }

    private def calc(speed: Float, max: Float, rate: Float) = (max - speed) * rate

    private def falling = calc(_speed.y, fallingMax, fallingRate)

  }
  
  /** shuts the actor belonging to this Entity down */
  private[sidescrolling] def shutdown(){
    val context = TypedActor.context
    context.stop(context.self)
  }
  
  private[sidescrolling] override def data = entityData

}

private object Entity3DImpl {

  object Status extends Enumeration {
    val Walking, Stopped = Value
  }
}