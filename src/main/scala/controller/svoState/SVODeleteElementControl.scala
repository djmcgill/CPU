package controller.svoState

import com.jme3.input.MouseInput
import com.jme3.input.controls.MouseButtonTrigger
import com.jme3.math.Vector3f
import logic.voxels._

import scala.collection.mutable

class SVODeleteElementControl extends AbstractSVOInsertionControl {
  override val name = "DELETE FROM FACE"
  override val triggers = Seq(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT))
  override val node: SVONode = Full(None)
  override val insertion: Boolean = false
}
