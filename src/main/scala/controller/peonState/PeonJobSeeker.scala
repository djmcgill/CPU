package controller.peonState


import com.jme3.bullet.control.BetterCharacterControl
import com.jme3.math.{FastMath, Vector3f}
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.control.AbstractControl
import logic.voxels.{AStar, SvoNavGrid}

import scala.util.Random

class PeonJobSeeker(peonId: Long, jobManager: JobManager, svoNavGrid: SvoNavGrid) extends AbstractControl {
  val WalkSpeedMult = 3.0f
  var currentJob: JobState = Idle()
  var currentPath = List[Vector3f]()
  lazy val control = spatial.getControl(classOf[BetterCharacterControl])

  override def controlRender(renderManager: RenderManager, viewPort: ViewPort): Unit = {}

  override def controlUpdate(v: Float): Unit = {
    // Update job state
    if (currentPath.isEmpty) {
      currentJob match {
        case Idle() => gotoIdleLocation()
        case InteractWithBlock(position) =>
          println(s"trying to interact with $position")
          val currentPosition = spatial.getWorldTranslation
          val maybePath = AStar.pathToInWorld(currentPosition, position, svoNavGrid)
          currentPath = maybePath.getOrElse(Nil)
      }
    }

    // Move as specified by the jobstate
    val maybeCurrentTarget: Option[Vector3f] = currentPath match {
      case Nil => None // spin slightly in place?
      case nextTarget :: rest =>
        if (spatial.getWorldTranslation.distance(nextTarget) < 0.1f) {
          if (rest.isEmpty) {
            None
          } else {
            currentPath = rest
            Some(rest.head)
          }
        } else {
          Some(nextTarget)
        }
    }
    maybeCurrentTarget foreach actuallyWalkTowardsTarget(spatial.getWorldTranslation)
  }

  def gotoIdleLocation(): Unit = {
    val idleCenter = new Vector3f(0.6f, 0.5f, 0.6f) mult math.pow(2, svoNavGrid.svo.height).toFloat
    val idleRadius = 10
    if (spatial.getWorldTranslation.distance(idleCenter) <= idleRadius) {
      currentJob = jobManager.peonRequestJob(peonId)
    } else {
      // Move towards a random point in the idle area. When there, do nothing.
      val angle = Random.nextFloat() * FastMath.TWO_PI
      val distance = Random.nextFloat() * idleRadius
      val point = idleCenter add new Vector3f(distance * math.cos(angle).toFloat, 0, distance * math.sin(angle).toFloat)

      val currentPosition = spatial.getWorldTranslation
      val maybePath = AStar.pathToInWorld(currentPosition, point, svoNavGrid)
      currentPath = maybePath.getOrElse(Nil)
    }
  }

  def actuallyWalkTowardsTarget(currentLocation: Vector3f)(currentTarget: Vector3f): Unit = {
    val facingTarget = currentTarget
    facingTarget.y = currentLocation.y
    spatial.lookAt (facingTarget, Vector3f.UNIT_Y)

    val walkDirection = currentTarget subtract currentLocation
    walkDirection.y = 0
    walkDirection.normalizeLocal ()
    control.jump()
    control.setWalkDirection(walkDirection mult WalkSpeedMult)
  }
}
