package controller

import com.jme3.input.MouseInput
import com.jme3.input.controls.MouseButtonTrigger
import logic.voxels.{RayCaster, SVO}

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

    result foreach {case (_, path) => svo.deleteNodePath(path)}
  }


}
