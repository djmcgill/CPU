import com.jme3.app.SimpleApplication
import com.jme3.input.ChaseCamera
import controller.Controls
import logic.GameState

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
    val chaseCam = new ChaseCamera(cam, gs.cameraTarget, inputManager)
    rootNode.attachChild(gs.cameraTarget)
    new Controls(inputManager, gs).initKeys()
  }



}