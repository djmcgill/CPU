import com.jme3.app.SimpleApplication
import com.jme3.input.{KeyInput, ChaseCamera}
import com.jme3.input.controls.{AnalogListener, KeyTrigger}
import com.jme3.material.Material
import com.jme3.math.Vector3f
import com.jme3.scene.{Node, Geometry}
import com.jme3.scene.shape.Box
import com.jme3.math.ColorRGBA
import logic.GameState

import rendering.{GameStateRenderer, SVORenderer}
import logic.voxels.SVO

object CPU extends SimpleApplication {
  var gs: GameState = GameState.initialGameState
  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  @Override
  def simpleInitApp() {

    val gsRenderer = new GameStateRenderer(assetManager, rootNode)
    flyCam.setEnabled(false)
    val chaseCam = new ChaseCamera(cam, gs.cameraTarget, inputManager)
    rootNode.attachChild(gs.cameraTarget)


    gsRenderer.render(gs)
    initKeys()

    // TODO: have controls change the camera
  }

  def initKeys() = {
    // TODO: hard coded strings are bad mmkay?
    val cameraTargetKeyNames = Seq(
      ("CAMERA TARGET LEFT", KeyInput.KEY_LEFT),
      ("CAMERA TARGET RIGHT", KeyInput.KEY_RIGHT),
      ("CAMERA TARGET FORWARD", KeyInput.KEY_UP),
      ("CAMERA TARGET BACKWARD", KeyInput.KEY_DOWN),
      ("CAMERA TARGET UP", KeyInput.KEY_PGUP),
      ("CAMERA TARGET DOWN", KeyInput.KEY_PGDN))

    for ((name, key) <- cameraTargetKeyNames) {
      inputManager.addMapping(name, new KeyTrigger(key))
    }

    val analogListener = new AnalogListener() {
      def onAnalog(name: String, value: Float, tpf: Float) = name match {
        case "CAMERA TARGET LEFT" => gs.cameraTarget.move(-1 * tpf, 0, 0)
        case "CAMERA TARGET RIGHT" => gs.cameraTarget.move(1 * tpf, 0, 0)
        case "CAMERA TARGET FORWARD" => gs.cameraTarget.move(0, 0, -1 * tpf)
        case "CAMERA TARGET BACKWARD" => gs.cameraTarget.move(0, 0, 1 * tpf)
        case "CAMERA TARGET UP" => gs.cameraTarget.move(1 * tpf, 0, 0)
        case "CAMERA TARGET DOWN" => gs.cameraTarget.move(-1 * tpf, 0, 0)
      }
    }
    val names: Seq[String] = cameraTargetKeyNames map (_._1)
    inputManager.addListener(analogListener, names: _*)
  }



}