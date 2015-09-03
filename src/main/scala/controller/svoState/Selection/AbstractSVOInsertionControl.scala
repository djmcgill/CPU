package controller.svoState.selection

import com.jme3.math.Vector3f
import controller.AbstractActionListenerState
import controller.svoState.SVOSpatialState
import logic.voxels._

/**
 * This class will modify the SVO with a given function.
 * It can modify either the block actually clicked on (with insertion = false)
 * Or the block that's created on the side of the block that is clicked on.
 *
 * It returns the path to the node that needs to be recreated (if any).
 */

// TODO: change so that this class returns the block currently being hovered on.
abstract class AbstractSVOInsertionControl extends AbstractActionListenerState {
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
      // convert from world coordinates to the (0,0,0),(1,1,1) cube of the svo

      val maxHeight: Int = app.getRootNode.getUserData[Int]("maxHeight")
      val relativeHitPosition = Octant.globalToLocal(maxHeight, absoluteHitPosition, path)

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
      val insertPosition = absoluteHitPosition add onBlockOrNewBlock
      val queueManager = app.getStateManager.getState[SVOSpatialState](classOf[SVOSpatialState])
      queueManager.requestSVOInsertion(node, insertPosition)
    }
  }
}
