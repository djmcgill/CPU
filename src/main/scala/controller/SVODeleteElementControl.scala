package controller

import com.jme3.input.MouseInput
import com.jme3.input.controls.MouseButtonTrigger
import com.jme3.math.Vector3f
import logic.voxels.{Dirt, Block, RayCaster, SVO}

/**
 * This control implements blocks disappearing from the SVO when they're shift-clicked on.
 * It needs to update both the SVO itself and also the renderer.
 */

class SVODeleteElementControl extends AbstractActionListenerState {
  override val name = "DELETE CUBE"
  override val triggers = Seq(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT))
  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    val svo = app.getRootNode.getUserData[SVO]("svo")

    if (!isPressed) return
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
      val edges = List(
        (1.0f, relativeHitPosition.x), (0.0f, relativeHitPosition.x),
        (1.0f, relativeHitPosition.y), (0.0f, relativeHitPosition.y),
        (1.0f, relativeHitPosition.z), (0.0f, relativeHitPosition.z))
      val diffs = edges map {case (x: Float, y: Float) => math.abs(x - y)}
      val indexOfSmallestDiff: Int = diffs.zipWithIndex.minBy(_._1)._2
      val adjustment: Vector3f = indexOfSmallestDiff match {
        case 0 => Vector3f.UNIT_X mult 0.0001f
        case 1 => Vector3f.UNIT_X mult -0.0001f
        case 2 => Vector3f.UNIT_Y mult 0.0001f
        case 3 => Vector3f.UNIT_Y mult -0.0001f
        case 4 => Vector3f.UNIT_Z mult 0.0001f
        case 5 => Vector3f.UNIT_Z mult -0.0001f
        case _ => throw new IllegalStateException
      }

      // May possibly need to convert adjustment from parentSpace
      // to the scale of the smallest voxel
      svo.deleteNodeAt(absoluteHitPosition subtract adjustment, 0)
    }
  }


}
