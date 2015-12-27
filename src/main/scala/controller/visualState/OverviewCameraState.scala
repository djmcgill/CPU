package controller.visualState

import com.jme3.app._
import com.jme3.app.state._
import com.jme3.input._
import com.jme3.input.controls._
import com.jme3.math.{FastMath, Vector3f}
import com.jme3.scene.Node
import controller.AbstractAnalogListenerState

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

class OverviewCameraState(maxHeight: Int) extends AbstractAnalogListenerState {
  private val PanMultiplier = 10

  private val cameraTarget: Node = new Node("Overview Camera Target")
  private var chaseCam: ChaseCamera = _

  private val cameraTranslations = {
    import OverviewCameraState._
    Map((TargetLeftName    , Vector3f.UNIT_X mult -PanMultiplier),
        (TargetRightName   , Vector3f.UNIT_X mult  PanMultiplier),
        (TargetForwardName , Vector3f.UNIT_Z mult -PanMultiplier),
        (TargetBackwardName, Vector3f.UNIT_Z mult  PanMultiplier),
        (TargetUpName      , Vector3f.UNIT_Y mult  PanMultiplier),
        (TargetDownName    , Vector3f.UNIT_Y mult -PanMultiplier))
  }

  override val analogNames = cameraTranslations.keys.toList

  // When a key is pressed, move the camera according to the offset specified in cameraTranslations
  override def analog(name: String, value: Float, tpf: Float) =
      cameraTranslations.get(name) foreach (offset => cameraTarget.move(offset mult tpf))

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)

    // Create and register the camera
    app.getFlyByCamera.setEnabled(false)
    chaseCam = new ChaseCamera(app.getCamera, cameraTarget, app.getInputManager)
    chaseCam.setHideCursorOnRotate(true)
    chaseCam.setZoomSensitivity(50)

    chaseCam.setDefaultHorizontalRotation(FastMath.HALF_PI)
    chaseCam.setMaxDistance(1000)
    chaseCam.setDefaultDistance(15)

    app.getRootNode.attachChild(cameraTarget)
    val scale = math.pow(2, maxHeight).toFloat
    cameraTarget.setLocalTranslation(scale/2, scale/2 + 5, scale/2)
    chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT))
  }

  override def cleanup(): Unit = {
    super.cleanup()
    app.getFlyByCamera.setEnabled(true)
    app.getRootNode.detachChild(cameraTarget)
  }
}
