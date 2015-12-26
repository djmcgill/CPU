package controller.blockState

import com.jme3.math.Vector3f
import controller._
import controller.svoState.SvoManager
import logic.voxels.{Block, Full, SVO}

class BlockManager extends GameState {
  lazy val spatialState: SvoManager = app.getStateManager.getState(classOf[SvoManager])
  lazy val svo: SVO = app.getRootNode.getUserData[SVO]("svo")

  // Maybe return a boolean if it was a valid placement or not?
  def requestPlacement(block: Block, location: Vector3f): Unit = {
    val newState = PlacementPending(block, location)
    val maybeCurrentState: Option[BlockState] = svo.getNodeAt(location, 0) match {
      case Some(Full(maybeBlockState)) => maybeBlockState
      case _ => None
    }
    val pointlessOrder = maybeCurrentState match {
      case Some(blockState : RemovalPending)   if blockState.data == block => true
      case Some(blockState : RemovalScheduled) if blockState.data == block => true
      case _ => false
    }
    val cheatMode = app.getRootNode.getUserData[Boolean]("cheatMode")
    if (cheatMode || pointlessOrder) {
      placementJobReady(newState)
    } else {
      println(s"TODO: add job at $location to queue") // TODO
      spatialState.requestSVOInsertion(Some(newState), location)
    }
  }
  def placementJobReady(state: BlockState): Unit = {
    val location = state match {
      case PlacementScheduled(_, loc, worker) =>
        println(s"to remove job at $loc from worker $worker")
        loc
      case PlacementPending(_, loc) => loc
      case _ => throw new IllegalArgumentException(s"Unallowed state $state")    }

    val collision: Boolean = spatialState.svoPhysicsState.svoSpatialCollidesWithEntity(0, location)
    if (collision) {
      ??? // TODO: We tried to insert but can't. It's still a valid job however, so we shouldn't just throw it away.
    } else {
      spatialState.requestSVOInsertion(Some(Placed(state.data)), location)
    }
  }

  def requestRemoval(location: Vector3f): Unit = {
    val maybeCurrentState = svo.getNodeAt(location, 0) match {
      case Some(Full(result)) => result
      case _ => None
    }
    val maybeNewState = maybeCurrentState map (currentState => PlacementPending(currentState.data, location))

    val pointlessOrder = maybeCurrentState match {
      case Some(_: PlacementPending) => true // TODO: remove job from queue
      case Some(_: PlacementScheduled) => true // TODO: remove job from worker
      case _ => false
    }

    val cheatMode = app.getRootNode.getUserData[Boolean]("cheatMode")
    if (cheatMode || pointlessOrder) {
      spatialState.requestSVOInsertion(None, location)
    } else {
      println("TODO: register removal request with job queue") // TODO
      spatialState.requestSVOInsertion(maybeNewState, location)
    }
  }

  def removalReady(state: BlockState): Unit = {
    val location = state match {
      case RemovalPending(_, loc) => loc // remove from job queue
      case RemovalScheduled(_, loc, _) => loc // remove from worker
      case _ => throw new IllegalArgumentException(s"Unallowed state $state")
    }
    spatialState.requestSVOInsertion(None, location)
  }

  def assignWorker(state: BlockState, workerID: Int): BlockState = ???
  def placementJobFailed(state: BlockState): Unit = ???
  def removalJobFailed(state: BlockState): BlockState = ???
}