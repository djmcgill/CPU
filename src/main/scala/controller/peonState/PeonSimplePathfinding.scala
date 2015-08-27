package controller.peonState

import java.util.concurrent.TimeoutException

import com.jme3.bullet.control.BetterCharacterControl
import com.jme3.math.{Quaternion, Vector3f}
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.control.AbstractControl
import com.jme3.system.lwjgl.LwjglTimer

import scala.concurrent.Promise
import scala.util.Success

class PeonSimplePathfinding(targetPosition: Vector3f, finish: Promise[Boolean], timeoutSeconds: Float) extends AbstractControl{

  // TODO: have world scale global variable
  // TODO: make timeout optional
  // TODO: maybe an action to undertake when succeeded?
  val CloseEnough = 0.02f
  val CloseEnoughSquared = CloseEnough * CloseEnough
  val timer = new LwjglTimer()

  override def controlRender(renderManager: RenderManager, viewPort: ViewPort): Unit = {}

  override def controlUpdate(tpf: Float): Unit = {
    val control = spatial.getControl[BetterCharacterControl](classOf[BetterCharacterControl])

    if (timer.getTimeInSeconds > timeoutSeconds) {
      finish.failure(new TimeoutException)
      control.setWalkDirection(Vector3f.ZERO)
      spatial.removeControl(this)
    } else {
      val stillToGo = targetPosition subtract spatial.getLocalTranslation
      stillToGo.y = 0
      if (stillToGo.lengthSquared < CloseEnoughSquared) {
        finish.complete(Success(true))
        control.setWalkDirection(Vector3f.ZERO)
        spatial.removeControl(this)
      } else {
        val rotation = new Quaternion()
        rotation.lookAt(stillToGo.normalize, Vector3f.UNIT_Y)
        spatial.setLocalRotation(rotation)
        val walkDir = rotation mult Vector3f.UNIT_Z mult tpf mult 3000
        control.setWalkDirection(walkDir)
        // TODO: something about the peon's walk speed?
        // TODO: do we need to jump?
      }
    }
  }
}
