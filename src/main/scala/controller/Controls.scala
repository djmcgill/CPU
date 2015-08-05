package controller

import com.jme3.input.{InputManager, KeyInput}
import com.jme3.input.controls.{AnalogListener, KeyTrigger}
import com.jme3.math.Vector3f
import logic.GameState

/**
 * Manages the controls.
 * TODO: make cameraTranslations a Map so that you can change keybindings,
 * and change _3 to a params Array
 */
class Controls(inputManager: InputManager, gs: GameState) {
  def initKeys() = {
    val moveX = new Vector3f(10,  0,  0)
    val moveY = new Vector3f( 0, 10,  0)
    val moveZ = new Vector3f( 0,  0, 10)
    val cameraTranslations = Seq(
      ("CAMERA TARGET LEFT"    , KeyInput.KEY_LEFT , moveX mult -1),
      ("CAMERA TARGET RIGHT"   , KeyInput.KEY_RIGHT, moveX),
      ("CAMERA TARGET FORWARD" , KeyInput.KEY_UP   , moveZ mult -1),
      ("CAMERA TARGET BACKWARD", KeyInput.KEY_DOWN , moveZ),
      ("CAMERA TARGET UP"      , KeyInput.KEY_PGUP , moveY),
      ("CAMERA TARGET DOWN"    , KeyInput.KEY_PGDN , moveY mult -1))

    for ((name, key, _) <- cameraTranslations) {
      inputManager.addMapping(name, new KeyTrigger(key))
    }

    val analogListener = new AnalogListener() {
      def onAnalog(name: String, value: Float, tpf: Float) =
        cameraTranslations find (_._1 == name) match {
          case None =>
          case Some((_, _, offset)) => gs.cameraTarget.move(offset mult tpf)
        }
    }
    inputManager.addListener(
      analogListener,
      cameraTranslations map (_._1): _*)
  }
}
