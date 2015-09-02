package controller.peonState.jobs

import java.util.concurrent.TimeoutException

import com.jme3.bullet.control.BetterCharacterControl
import com.jme3.math.Vector3f
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.control.AbstractControl
import com.jme3.system.lwjgl.LwjglTimer

import scala.concurrent.Promise

class Idle(finish: Promise[Boolean], maybeTimeoutSeconds: Option[Float]) extends AbstractControl {
  val timer = new LwjglTimer()

  override def controlRender(renderManager: RenderManager, viewPort: ViewPort): Unit = {}

  override def controlUpdate(v: Float): Unit = {
    if (maybeTimeoutSeconds exists (timer.getTimeInSeconds > _)) {
      val control = spatial.getControl[BetterCharacterControl](classOf[BetterCharacterControl])
      finish.failure(new TimeoutException)
      control.setWalkDirection(Vector3f.ZERO)
      spatial.removeControl(this)
    }

  }
}
