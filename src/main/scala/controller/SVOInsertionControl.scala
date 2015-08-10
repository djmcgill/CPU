package controller

import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.control._

/**
 * This control implements new blocks appearing in the SVO when they're clicked on.
 * It needs to update both the SVO itself and also the renderer.
 */
object SVOInsertionControl extends AbstractControl {
  override def controlUpdate(tpf: Float) = {}
  override def controlRender(rm: RenderManager, vp: ViewPort) = {}
}
