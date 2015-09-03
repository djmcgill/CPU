package controller.svoState.selection

import logic.voxels._

/**
  * This control implements new blocks appearing in the SVO when they're clicked on.
  */
class SVOInsertElementControl extends AbstractSVOInsertionControl {
  override val names = List("PLACE DIRT")
  override val node: SVONode = {
    val block: Block = new Dirt()
    Full(Some(block))
  }
  override val insertion: Boolean = true
}
