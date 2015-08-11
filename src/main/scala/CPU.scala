import com.jme3.app.SimpleApplication
import controller.OverviewCameraControls
import logic.GameState

import rendering.GameStateRenderer

object CPU extends SimpleApplication {
  var gs: GameState = GameState.initialGameState
  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  override def simpleInitApp() {
    val gsRenderer = new GameStateRenderer(assetManager, rootNode)
    gsRenderer.render(gs)
    stateManager.attach(new OverviewCameraControls)
  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)
    // TODO: refresh the svo
    // TODO: now refresh only the changes to the svo
  }
}