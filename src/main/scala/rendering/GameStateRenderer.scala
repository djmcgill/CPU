package rendering

import com.jme3.asset.AssetManager
import com.jme3.scene.Node
import logic.GameState
import rendering.SVORenderer

/**
 * Created by David McGillicuddy on 05/08/2015.
 */
class GameStateRenderer(assetManager: AssetManager, rootNode: Node) {
  val svoRenderer = new SVORenderer(assetManager, rootNode)
  def render(gs: GameState): Unit = {
    svoRenderer.render(gs.svo)
  }

}
