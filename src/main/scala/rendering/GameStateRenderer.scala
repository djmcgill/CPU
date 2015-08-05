package rendering

import com.jme3.asset.AssetManager
import com.jme3.scene.Node
import logic.GameState
import rendering.SVORenderer

/**
 * Renders a logic.GameState
 */
class GameStateRenderer(assetManager: AssetManager, rootNode: Node) {
  val svoRenderer = new SVORenderer(assetManager, rootNode)
  def render(gs: GameState): Unit = {
    svoRenderer.render(gs.svo)
  }

}
