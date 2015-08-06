package logic

import com.jme3.scene.Node
import logic.voxels.SVO

/**
 * At the moment, the gamestate consists only of the voxels and the camera.
 */
object GameState {
  val initialGameState = new GameState (SVO.minimalSubdivided, new Node())
}

class GameState (var svo: SVO, val cameraTarget: Node) {}
