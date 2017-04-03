package controller.blockState

import com.jme3.math.Vector3f
import logic.voxels.Block

// State transitions
sealed abstract class BlockState {
  val data: Block
  val isSolid: Boolean
  val isOpaque: Boolean
}

// States
case class Placed(data: Block) extends BlockState {
  override val isSolid = true; override val isOpaque = true
}
case class PlacementPending(data: Block, location: Vector3f) extends BlockState {
  override val isSolid = false
  override val isOpaque = false
  val assignedPeon: Option[Int] = None
}
case class RemovalPending(data: Block, location: Vector3f) extends BlockState {
  override val isSolid = true
  override val isOpaque = false
  val assignedPeon: Option[Int] = None
}
