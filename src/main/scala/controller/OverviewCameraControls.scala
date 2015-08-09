package controller

import com.jme3.app.Application
import com.jme3.app.state.{AppStateManager, AbstractAppState}
import com.jme3.input.{InputManager, KeyInput}
import com.jme3.input.controls.{AnalogListener, KeyTrigger}
import com.jme3.math.Vector3f
import logic.GameState

/**
 * Manages the controls.
 */
class OverviewCameraControls extends AbstractAppState {
  // TODO: changeable keybindings here
  // TODO: move the cameraTarget here

  val moveX = Vector3f.UNIT_X mult 10
  val moveY = Vector3f.UNIT_Y mult 10
  val moveZ = Vector3f.UNIT_Z mult 10
  val cameraTranslations = Seq(
    ("CAMERA TARGET LEFT"    , KeyInput.KEY_LEFT , moveX mult -1),
    ("CAMERA TARGET RIGHT"   , KeyInput.KEY_RIGHT, moveX),
    ("CAMERA TARGET FORWARD" , KeyInput.KEY_UP   , moveZ mult -1),
    ("CAMERA TARGET BACKWARD", KeyInput.KEY_DOWN , moveZ),
    ("CAMERA TARGET UP"      , KeyInput.KEY_PGUP , moveY),
    ("CAMERA TARGET DOWN"    , KeyInput.KEY_PGDN , moveY mult -1))

  override def initialize(stateManager: AppStateManager, app: Application): Unit = {
    super.initialize(stateManager, app)

    for ((name, key, _) <- cameraTranslations) {
      app.getInputManager.addMapping(name, new KeyTrigger(key))
    }

    val analogListener = new AnalogListener() {
      def onAnalog(name: String, value: Float, tpf: Float) =
        cameraTranslations find (_._1 == name) match {
          case None =>
          case Some((_, _, offset)) =>
            val gs: GameState = ???
            gs.cameraTarget.move(offset mult tpf)
        }
    }
    app.getInputManager.addListener(
      analogListener,
      cameraTranslations map (_._1): _*)
  }

  override def cleanup(): Unit = {
    super.cleanup()
    // TODO: remove the listeners and mappings and stuff
  }
}
