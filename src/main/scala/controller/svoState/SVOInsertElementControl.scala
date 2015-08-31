package controller.svoState

import com.jme3.input.MouseInput
import com.jme3.input.controls.MouseButtonTrigger
import com.jme3.math.Vector3f
import logic.voxels._

import scala.collection.mutable

/**
  * This control implements new blocks appearing in the SVO when they're clicked on.
  */
class SVOInsertElementControl extends AbstractSVOInsertionControl {
  override val name = "INSERT ONTO FACE"
  override val triggers = Seq(new MouseButtonTrigger(MouseInput.BUTTON_LEFT))
  override val node: SVONode = {
    val block: Block = new Dirt()
    Full(Some(block))
  }
  override val insertion: Boolean = true
}
