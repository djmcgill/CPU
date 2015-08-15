package controller

import com.jme3.input.MouseInput
import com.jme3.input.controls.MouseButtonTrigger
import logic.voxels._

import scala.collection.mutable

class SVODeleteElementControl(queue: mutable.Queue[(SVONode, List[Octant])])
    extends AbstractSVOInsertionControl(queue) {
  override val name = "DELETE FROM FACE"
  override val triggers = Seq(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT))
  override val node: SVONode = Full(None)
  override val insertion: Boolean = false
}
