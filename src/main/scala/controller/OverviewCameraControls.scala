package controller

import com.jme3.app._
import com.jme3.app.state._
import com.jme3.input._
import com.jme3.input.controls._
import com.jme3.math.Vector3f
import com.jme3.scene.control.CameraControl.ControlDirection
import com.jme3.scene.{CameraNode, Node}

/**
 * Manages the controls.
 */
class OverviewCameraControls extends AbstractAppState {
  // TODO: changeable keybindings here
  var cameraTarget: Node = new Node("Overview Camera Target")
  var app: SimpleApplication = _

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

  val analogListener = new AnalogListener() {
    override def onAnalog(name: String, value: Float, tpf: Float) =
      cameraTranslations find (_._1 == name) foreach {case (_, _, offset) =>
        cameraTarget.move(offset mult tpf)}
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app = superApp match {
      case simple: SimpleApplication => simple
      case _ => throw new ClassCastException
    }

    // Create and register the camera
    app.getFlyByCamera.setEnabled(false)
    val chaseCam = new ChaseCamera(app.getCamera, cameraTarget, app.getInputManager)
    app.getRootNode.attachChild(cameraTarget)
    chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT))

    // Set up the key mappings and corresponding actions.
    cameraTranslations foreach {case (name, key, _) =>
      app.getInputManager.addMapping(name, new KeyTrigger(key))}

    app.getInputManager.addListener(analogListener, cameraTranslations map (_._1): _*)
  }

  override def cleanup(): Unit = {
    super.cleanup()
    app.getFlyByCamera.setEnabled(true)
    app.getInputManager.removeListener(analogListener)

    cameraTranslations foreach {case (name,_,_) =>
      app.getInputManager.deleteMapping(name)}

    app.getRootNode.detachChild(cameraTarget)
  }
}
