import com.jme3.app.SimpleApplication
import com.jme3.input.ChaseCamera
import com.jme3.material.Material
import com.jme3.math.Vector3f
import com.jme3.scene.{Node, Geometry}
import com.jme3.scene.shape.Box
import com.jme3.math.ColorRGBA
import logic.GameState

import rendering.{GameStateRenderer, SVORenderer}
import logic.voxels.SVO

object CPU extends SimpleApplication {

  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  @Override
  def simpleInitApp() {
    val gs: GameState = GameState.initialGameState
    val gsRenderer = new GameStateRenderer(assetManager, rootNode)
    flyCam.setEnabled(false)
    val chaseCam = new ChaseCamera(cam, gs.cameraTarget, inputManager)
    rootNode.attachChild(gs.cameraTarget)


    gsRenderer.render(gs)

    // TODO: have controls change the camera
  }
}