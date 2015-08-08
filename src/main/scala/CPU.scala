import com.jme3.app.SimpleApplication
import com.jme3.input.{MouseInput, ChaseCamera}
import com.jme3.input.controls.{ActionListener, MouseButtonTrigger}
import com.jme3.math.Vector3f
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

        // TODO: cam.getDirection only looks at what the CAMERA is looking at, not useful here.
        val rayDirection: Vector3f = ???

        printf("origin: (%f, %f, %f)\n", rayOrigin.x, rayOrigin.y, rayOrigin.z)
        printf("direction: (%f, %f, %f)\n", rayDirection.x, rayDirection.y, rayDirection.z)
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