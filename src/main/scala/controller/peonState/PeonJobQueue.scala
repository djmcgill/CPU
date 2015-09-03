package controller.peonState

import com.jme3.math.Vector3f
import com.jme3.scene.control.Control
import controller.AbstractActionListenerState
import controller.peonState.jobs.PeonSimplePathfinding
import controller.svoState.SVOSpatialState
import logic.voxels._

import scala.collection.mutable
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

class PeonJobQueue() extends AbstractActionListenerState {
  private val jobQueue: mutable.Queue[Control] = new mutable.Queue()

  def requestBlockPlacement(globalPosition: Vector3f) = {
    val svo = app.getRootNode.getUserData[SVO]("svo")
    val svoSpatialState = app.getStateManager.getState[SVOSpatialState](classOf[SVOSpatialState])

    // have some way to cancel the job maybe?
    val promise: Promise[Boolean] = Promise()

    promise.future.onSuccess { case true =>
        svo.getNodeAt(globalPosition, 0) foreach {
            case Full(Some(Phantom(block))) =>
              svoSpatialState.requestSVOInsertion(Full(Some(block)), globalPosition)
            case _ =>
        }
    }
    val pathfinding = new PeonSimplePathfinding(globalPosition, promise, None)
    jobQueue.enqueue(pathfinding)
  }

  def requestJob() = {
    if (jobQueue.isEmpty) {idleJob} else {jobQueue.dequeue()}
  }

  // Go to a corner and wait for a time.
  private def idleJob: Control = {
    val svoWidth = math.pow(2, app.getRootNode.getUserData[Int]("maxHeight")).toFloat
    val target: Vector3f = new Vector3f(svoWidth/2,svoWidth/2 + 2,svoWidth/2)
    val promise = Promise[Boolean]()
    val timeout = Some(10f)
    val goToCorner = new PeonSimplePathfinding(target, promise, timeout)
    goToCorner
  }

  /** The actual action to perform */
  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    // find the target from the mouse
    if (!isPressed) {return}
    val svo: SVO = app.getRootNode.getUserData[SVO]("svo")
    val rayOrigin = app.getCamera.getLocation
    val click2d = app.getInputManager.getCursorPosition
    def worldCoordsAtZ(z: Float) = app.getCamera.getWorldCoordinates(click2d, z)
    val rayDirection = (worldCoordsAtZ(1) subtractLocal worldCoordsAtZ(0)).normalizeLocal
    val result = RayCaster.cast(rayOrigin, rayDirection, svo)

    result foreach { case (globalHitPosition, path) =>
      val maxHeight: Int = app.getRootNode.getUserData[Int]("maxHeight")
      val relativeHitPosition = Octant.globalToLocal(maxHeight, globalHitPosition, path)

      // We have a point on the face of a cube, and we want to nudge it over
      // the boundary so that the insert position corresponds to the cube touching that face.
      val diffs = for (
        position <- Array(relativeHitPosition.x, relativeHitPosition.y, relativeHitPosition.z);
        edge <- Array(1.0f, 0.0f)
      ) yield math.abs (edge - position)

      val indexOfSmallestDiff: Int = diffs.zipWithIndex.minBy(_._1)._2
      val EPS = 0.0001f
      val adjustment: Vector3f = indexOfSmallestDiff match {
        case 0 => Vector3f.UNIT_X mult EPS
        case 1 => Vector3f.UNIT_X mult -EPS
        case 2 => Vector3f.UNIT_Y mult EPS
        case 3 => Vector3f.UNIT_Y mult -EPS
        case 4 => Vector3f.UNIT_Z mult EPS
        case 5 => Vector3f.UNIT_Z mult -EPS
        case _ => throw new IllegalStateException
      }
      val insertPosition = globalHitPosition add adjustment
      requestBlockPlacement(insertPosition)
    }

  }

  override val names = List("PLACE PHANTOM DIRT")
}
