package controller

import _root_.CPU._
import com.jme3.app.Application
import com.jme3.app.state.{AppStateManager, AbstractAppState}
import com.jme3.input.MouseInput
import com.jme3.input.controls.{MouseButtonTrigger, ActionListener}
import com.jme3.math.Vector3f
import logic.voxels.RayCaster

class SelectionCameraControls extends AbstractAppState {

  override def initialize(stateManager: AppStateManager, app: Application): Unit = {
    super.initialize(stateManager, app)
/*
    val cubeClicker = new ActionListener() {
      def onAction(name: String, keyPressed: Boolean, tpf: Float): Unit = {
        val rayOrigin = cam.getLocation

        val click2d = inputManager.getCursorPosition
        def worldCoordsAtZ(z: Float) = cam.getWorldCoordinates(click2d, z)
        val rayDirection = (worldCoordsAtZ(1) subtractLocal worldCoordsAtZ(0)).normalizeLocal

        val result = RayCaster.cast(rayOrigin, rayDirection, gs.svo)
        result match {
          case None => println("none")
          case Some((hitResult, path)) =>
            printf("path = ")
            for (o <- path) {printf("%d", o.ix)}
            println()
        }
      }
    }

    inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT))
    inputManager.addListener(cubeClicker, "Click")
 */
  }

  override def cleanup(): Unit = {
    super.cleanup()
    // TODO: unregister all the handlers and stuff
  }
}
