package controller

import com.jme3.input.MouseInput
import com.jme3.input.controls.MouseButtonTrigger
import com.jme3.math.Vector3f
import logic.voxels._

import scala.collection.mutable

/**
  * This control implements new blocks appearing in the SVO when they're clicked on.
  */
class SVOInsertElementControl(queue: mutable.Queue[(SVONode, Vector3f)])
    extends AbstractSVOInsertionControl(queue) {
  override val name = "INSERT ONTO FACE"
  override val triggers = Seq(new MouseButtonTrigger(MouseInput.BUTTON_LEFT))
  override val node: SVONode = Full(Some(new Dirt()))
  override val insertion: Boolean = true
}
