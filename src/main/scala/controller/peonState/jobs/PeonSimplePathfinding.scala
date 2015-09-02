package controller.peonState.jobs

import java.util.concurrent.TimeoutException

import com.jme3.bullet.control.BetterCharacterControl
import com.jme3.math.{Quaternion, Vector3f}
import com.jme3.renderer.{RenderManager, ViewPort}
import com.jme3.scene.control.AbstractControl
import com.jme3.system.lwjgl.LwjglTimer

import scala.concurrent.Promise
import scala.util.Success

class PeonSimplePathfinding(
    targetPosition: Vector3f,
    finish: Promise[Boolean],
    maybeTimeoutSeconds: Option[Float])
  extends AbstractControl{

  val CloseEnoughSquared = 1.1f
  val TooCloseSquared = 0.5f
  val timer = new LwjglTimer()

  override def controlRender(renderManager: RenderManager, viewPort: ViewPort): Unit = {}

  override def controlUpdate(tpf: Float): Unit = {
    val control = spatial.getControl[BetterCharacterControl](classOf[BetterCharacterControl])

    if (maybeTimeoutSeconds exists (timer.getTimeInSeconds > _)) {
      finish.failure(new TimeoutException)
      control.setWalkDirection(Vector3f.ZERO)
      spatial.removeControl(this)
    } else {
      val stillToGo = targetPosition subtract spatial.getLocalTranslation
      stillToGo.y = 0
      if (stillToGo.lengthSquared < TooCloseSquared) {
        // Back off a bit
        val rotation = new Quaternion()
        rotation.lookAt(stillToGo.normalize mult -1, Vector3f.UNIT_Y)
        spatial.setLocalRotation(rotation)
        val walkDir = rotation mult Vector3f.UNIT_Z mult tpf mult 3000
        control.setWalkDirection(walkDir)
        control.jump()

      } else if (stillToGo.lengthSquared < CloseEnoughSquared) {
        // We're done
        finish.complete(Success(true))
        control.setWalkDirection(Vector3f.ZERO)
        spatial.removeControl(this)

      } else {
        // Go towards the goal
        val rotation = new Quaternion()
        rotation.lookAt(stillToGo.normalize, Vector3f.UNIT_Y)
        spatial.setLocalRotation(rotation)
        val walkDir = rotation mult Vector3f.UNIT_Z mult tpf mult 3000
        control.setWalkDirection(walkDir)
        control.jump()
      }
    }
  }
}
