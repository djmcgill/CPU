package controller

import com.jme3.input.MouseInput
import com.jme3.input.controls.MouseButtonTrigger
import logic.voxels._

/**
 * This control implements new blocks appearing in the SVO when they're clicked on.
 * It needs to update both the SVO itself and also the renderer.
 */

object SVOInsertElementControl {
  def insertDirt(n: Option[SVONode]) = Full(Some(new Dirt()))
}

class SVOInsertElementControl extends AbstractSVOModificationControl(SVOInsertElementControl.insertDirt, true) {
  override val name = "INSERT ONTO FACE"
  override val triggers = Seq(new MouseButtonTrigger(MouseInput.BUTTON_LEFT))
}
