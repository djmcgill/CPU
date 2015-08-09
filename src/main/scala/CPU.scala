import com.jme3.app.SimpleApplication
import com.jme3.input.{MouseInput, ChaseCamera}
import com.jme3.input.controls.{ActionListener, MouseButtonTrigger}
import com.jme3.math.{Vector2f, Vector3f}
import logic.GameState
import logic.voxels.RayCaster

import rendering.GameStateRenderer

object CPU extends SimpleApplication {
  var gs: GameState = GameState.initialGameState
  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  override def simpleInitApp() {
    // TODO: this needs to refresh itself when the SVO changes, do it in simpleUpdate()
    val gsRenderer = new GameStateRenderer(assetManager, rootNode)
    gsRenderer.render(gs)

    // TODO: move this to OverviewCameraControls
    flyCam.setEnabled(false)
    new ChaseCamera(cam, gs.cameraTarget, inputManager)
    rootNode.attachChild(gs.cameraTarget)


    // TODO: register the AppStates OverviewCameraControls and SelectionCameraControls


  }

  override def simpleUpdate(tpf: Float): Unit = {
    // TODO: refresh the svo
    // TODO: only refresh the changes to the svo
  }
}

/*
origin: (21.682274, 9.949214, -9.318962)
direction: (-0.847370, -0.381992, 0.368845)
path =

origin: (21.682274, 9.949214, -9.318962)
direction: (-0.837447, -0.400143, 0.372247)
none


origin: (-1.010584, 0.000000, 6.513712)
direction: (0.120898, 0.029150, -0.992237)
none

origin: (-1.010584, 0.000000, 6.513712)
direction: (0.231091, 0.057142, -0.971253)
path =


*/