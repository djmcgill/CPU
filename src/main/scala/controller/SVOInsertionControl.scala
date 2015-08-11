package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.input.MouseInput
import com.jme3.input.controls.{ActionListener, MouseButtonTrigger}
import com.jme3.math.Vector3f
import logic.voxels._

/**
 * This control implements new blocks appearing in the SVO when they're clicked on.
 * It needs to update both the SVO itself and also the renderer.
 */
class SVOInsertionControl(svo: SVO) extends AbstractAppStateWithApp {
  val insertionKey = "INSERT ONTO FACE"

  def actionListener = new ActionListener {
    override def onAction(name: String, isPressed: Boolean, tpf: Float): Unit = {
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
        val elementToInsert: Block = new Dirt()
        svo.insertElementAt(Some(elementToInsert), absoluteHitPosition add adjustment, 0)
        svo.printSVO()
      }
    }
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getInputManager.addMapping(insertionKey, new MouseButtonTrigger(MouseInput.BUTTON_LEFT))
    app.getInputManager.addListener(actionListener, insertionKey)
  }

  override def cleanup(): Unit = {
    super.cleanup()
    app.getInputManager.deleteMapping(insertionKey)
    app.getInputManager.removeListener(actionListener)
  }
}
