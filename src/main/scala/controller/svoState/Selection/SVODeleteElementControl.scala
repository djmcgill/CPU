package controller.svoState.selection

import logic.voxels._

class SVODeleteElementControl extends AbstractSVOInsertionControl {
  override val name = "DELETE BLOCK"
  override val node: SVONode = Full(None)
  override val insertion: Boolean = false
}
