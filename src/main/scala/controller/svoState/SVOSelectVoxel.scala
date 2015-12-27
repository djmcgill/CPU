package controller.svoState

import com.jme3.math.Vector3f
import controller.{SvoState, AbstractActionListenerState}
import logic.voxels._

/**
 * This class will record the position of the voxel that the mouse is currently over.
 */

object SvoSelectVoxel {
  val SelectVoxelName = "SELECT VOXEL"
}

class SvoSelectVoxel extends AbstractActionListenerState with SvoState {
  val actionNames = List(SvoSelectVoxel.SelectVoxelName)
  var selectedVoxel: Option[Vector3f] = None

  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    if (!isPressed) {return}
    val rayOrigin = app.getCamera.getLocation
    val click2d = app.getInputManager.getCursorPosition
    def worldCoordsAtZ(z: Float) = app.getCamera.getWorldCoordinates(click2d, z)
    val rayDirection = (worldCoordsAtZ(1) subtractLocal worldCoordsAtZ(0)).normalizeLocal

    RayCaster.cast(rayOrigin, rayDirection, svo) match {
      case None => selectedVoxel = None
      case Some((absoluteHitPosition, path)) =>

        // convert from world coordinates to the (0,0,0),(1,1,1) cube of the svo
        val relativeHitPosition = Octant.globalToLocal(maxHeight, absoluteHitPosition, path)

        // We have a point on the face of a cube, and we want to nudge it over
        // the boundary so that the insert position is inside of the cube.
        val diffs = for (
          position <- Array(relativeHitPosition.x, relativeHitPosition.y, relativeHitPosition.z);
          edge <- Array(1.0f, 0.0f)
        ) yield math.abs (edge - position)

        val indexOfSmallestDiff: Int = diffs.zipWithIndex.minBy(_._1)._2
        val magnitude = 0.001f
        val axis = indexOfSmallestDiff/2 match {
          case 0 => Vector3f.UNIT_X
          case 1 => Vector3f.UNIT_Y
          case 2 => Vector3f.UNIT_Z
        }
        val sign = indexOfSmallestDiff%2 match {
          case 0 => -1
          case 1 => 1
        }
        val adjustment: Vector3f = axis mult magnitude * sign
        selectedVoxel = Some(absoluteHitPosition add adjustment)
    }
  }
}
