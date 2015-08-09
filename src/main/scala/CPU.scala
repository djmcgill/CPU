import com.jme3.app.SimpleApplication
import com.jme3.input.{MouseInput, ChaseCamera}
import com.jme3.input.controls.{ActionListener, MouseButtonTrigger}
import com.jme3.math.{Vector2f, Vector3f}
import controller.Controls
import logic.GameState
import logic.voxels.RayCaster

import rendering.GameStateRenderer

object CPU extends SimpleApplication {
  var gs: GameState = GameState.initialGameState
  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  @Override
  def simpleInitApp() {
    val gsRenderer = new GameStateRenderer(assetManager, rootNode)
    gsRenderer.render(gs)

    flyCam.setEnabled(false)
    new ChaseCamera(cam, gs.cameraTarget, inputManager)
    rootNode.attachChild(gs.cameraTarget)
    new Controls(inputManager, gs).initKeys()

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