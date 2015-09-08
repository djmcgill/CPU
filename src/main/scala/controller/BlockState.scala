package controller

import com.jme3.export.{JmeExporter, JmeImporter, Savable}
import com.jme3.math.Vector3f
import controller.svoState.SVOState
import logic.voxels.{Full, Block}

// Constructor
object BlockState extends SVOState with Savable {
  var cheatMode: Boolean = true

  def read(im: JmeImporter): Unit = ???
  def write(ex: JmeExporter): Unit = ???

  def requestPlacement(block: Block, location: Vector3f): BlockState = {
    val newState = PlacementPending(block, location)
    if (cheatMode) {
      BlockState.placementJobReady(newState)
    } else {
      spatialState.requestSVOInsertion(newState, location)
      ??? // add job to queue
    }
  }
  def assignWorker(state: BlockState, workerID: Int): BlockState = state match {
      case PlacementPending(data, location) => PlacementScheduled(data, location, workerID)
      case RemovalPending(data, location) => RemovalScheduled(data, location, workerID)
  }
  def placementJobFailed(state: BlockState): Unit = {
    state match {
      case PlacementScheduled(_, location, worker) =>
        ??? // remove job from worker
        spatialState.requestSVODeletion(location)

      case PlacementPending(_, location) =>
        spatialState.requestSVODeletion(location)
    }
  }
  def removalJobFailed(state: BlockState): BlockState = {
    state match {
      case RemovalScheduled(_, _, worker) =>
        ??? // remove job from worker
        Placed(state.data)
      case RemovalPending(_, _) =>
        Placed(state.data)
    }
  }
  def placementJobReady(state: BlockState): BlockState = {
    val location = state match {
      case PlacementScheduled(_, loc, worker) =>
        ??? // remove job from worker
        loc
      case PlacementPending(_, loc) => loc
    }
    val collision: Boolean = spatialState.svoPhysicsState.svoSpatialCollidesWithEntity(0, location)
    if (collision) {
      state
    } else {
      spatialState.requestSVOInsertion(state, location)
      Placed(state.data)
    }
  }
  def removalJobSucceeded(state: BlockState): Unit = {
    val location = state match {
      case RemovalPending(_, loc) => loc
      case RemovalScheduled(_, loc, workerID) =>
        ??? // remove job from worker
        loc
    }
    spatialState.requestSVODeletion(location)
  }
  def requestRemoval(state: BlockState, location: Vector3f): Option[BlockState] = {
    val pendingState = RemovalPending(state.data, location)
    if (cheatMode) {
      BlockState.removalJobSucceeded(pendingState)
      None
    } else {
      ??? // register with job queue
      Some(pendingState)
    }
  }
}

// State transitions
sealed abstract class BlockState {
  val data: Block
}

// States
case class Placed(data: Block) extends BlockState
case class PlacementPending(data: Block, location: Vector3f) extends BlockState
case class RemovalPending(data: Block, location: Vector3f) extends BlockState
case class PlacementScheduled(data: Block, location: Vector3f, assignedWorkerID: Int) extends BlockState
case class RemovalScheduled(data: Block, location: Vector3f, assignedWorkerID: Int) extends BlockState
