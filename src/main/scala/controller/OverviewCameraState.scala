package controller

import com.jme3.app._
import com.jme3.app.state._
import com.jme3.input._
import com.jme3.input.controls._
import com.jme3.math.{FastMath, Vector3f}
import com.jme3.scene.Node

object OverviewCameraState {
  val TargetLeftName     = "CAMERA TARGET LEFT"
  val TargetRightName    = "CAMERA TARGET RIGHT"
  val TargetForwardName  = "CAMERA TARGET FORWARD"
  val TargetBackwardName = "CAMERA TARGET BACKWARD"
  val TargetUpName       = "CAMERA TARGET UP"
  val TargetDownName     = "CAMERA TARGET DOWN"
  val ZoomInName         = ChaseCamera.ChaseCamZoomIn
  val ZoomOutName        = ChaseCamera.ChaseCamZoomOut
}

class OverviewCameraState extends AbstractAnalogListenerState {
  import OverviewCameraState._
  val cameraTarget: Node = new Node("Overview Camera Target")
  var chaseCam: ChaseCamera = _
  val zoomDistance = 10

  val moveX = Vector3f.UNIT_X mult 10
  val moveY = Vector3f.UNIT_Y mult 10
  val moveZ = Vector3f.UNIT_Z mult 10

  val cameraTranslations = List(
    (TargetLeftName    , moveX mult -1),
    (TargetRightName   , moveX),
    (TargetForwardName , moveZ mult -1),
    (TargetBackwardName, moveZ),
    (TargetUpName      , moveY),
    (TargetDownName    , moveY mult -1))

  override val analogNames = cameraTranslations map (_._1)

  // When a key is pressed, move the camera according to the offset specified in cameraTranslations
  override def analog(name: String, value: Float, tpf: Float) =
      cameraTranslations find (_._1 == name) foreach { case (_, offset) =>
        cameraTarget.move(offset mult tpf)
    }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)

    // Create and register the camera
    app.getFlyByCamera.setEnabled(false)
    chaseCam = new ChaseCamera(app.getCamera, cameraTarget, app.getInputManager)
    chaseCam.setHideCursorOnRotate(true)
    chaseCam.setZoomSensitivity(50)

    chaseCam.setDefaultHorizontalRotation(FastMath.HALF_PI)

    val maxHeight = app.getRootNode.getUserData[Int]("maxHeight")
    val scale = math.pow(2, maxHeight).toFloat
    chaseCam.setMaxDistance(1000)
    chaseCam.setDefaultDistance(15)

    app.getRootNode.attachChild(cameraTarget)
    cameraTarget.setLocalTranslation(scale/2, scale/2 + 5, scale/2)


    chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT))
  }

  override def cleanup(): Unit = {
    super.cleanup()
    app.getFlyByCamera.setEnabled(true)
    app.getRootNode.detachChild(cameraTarget)
  }
}
