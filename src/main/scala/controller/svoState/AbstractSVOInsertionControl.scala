package controller.svoState

import com.jme3.math.Vector3f
import controller.AbstractActionListenerState
import logic.voxels._

import scala.collection.mutable

/**
 * This class will modify the SVO with a given function.
 * It can modify either the block actually clicked on (with insertion = false)
 * Or the block that's created on the side of the block that is clicked on.
 *
 * It returns the path to the node that needs to be recreated (if any).
 */

// TODO: is passing around the queue the best way? Could we instead just let the Control call a function in enqueue?
abstract class AbstractSVOInsertionControl(queue: mutable.Queue[(SVONode, Vector3f)])
    extends AbstractActionListenerState {
  val node: SVONode
  val insertion: Boolean

  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    //super.action(name: String, isPressed, tpf)
    if (!isPressed) {return}
    val svo: SVO = app.getRootNode.getUserData[SVO]("svo")
    val rayOrigin = app.getCamera.getLocation
    val click2d = app.getInputManager.getCursorPosition
    def worldCoordsAtZ(z: Float) = app.getCamera.getWorldCoordinates(click2d, z)
    val rayDirection = (worldCoordsAtZ(1) subtractLocal worldCoordsAtZ(0)).normalizeLocal
    val result = RayCaster.cast(rayOrigin, rayDirection, svo)

    result foreach {case (absoluteHitPosition, path) =>
      // What's the hit position relative to the clicked on cube?
      // Note that this should be on its face.
      val relativeHitPosition = path.foldLeft(absoluteHitPosition){case (v, o) => o.toChildSpace(v)}

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
      val onBlockOrNewBlock = if (insertion) {adjustment} else {adjustment mult -1}
      queue.enqueue((node, absoluteHitPosition add onBlockOrNewBlock))
    }
  }
}
