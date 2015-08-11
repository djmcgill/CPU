package controller

import logic.voxels.{RayCaster, SVO}

/**
 * This control implements blocks disappearing from the SVO when they're shift-clicked on.
 * It needs to update both the SVO itself and also the renderer.
 */
class SVODeleteElementControl(svo: SVO) extends AbstractActionListenerState {
  override val name = "DELETE CUBE"
  override val triggers = Seq()
  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    if (!isPressed) return
    val rayOrigin = app.getCamera.getLocation
    val click2d = app.getInputManager.getCursorPosition
    def worldCoordsAtZ(z: Float) = app.getCamera.getWorldCoordinates(click2d, z)
    val rayDirection = (worldCoordsAtZ(1) subtractLocal worldCoordsAtZ(0)).normalizeLocal

    val result = RayCaster.cast(rayOrigin, rayDirection, svo)

    result foreach {case (_, path) => svo.deleteNodePath(path)}
  }


}
