package controller

import com.jme3.math.Vector3f

// Constructor
object PlacementState {
  def requestPlacement[T](location: Vector3f, toPlace: T): PlacementState[T] = {
    val cheatMode = ???
    if (cheatMode) {
      val state = new PlacementPending[T]()
      state.jobDone()
    } else {
      ??? // place a phantom block in the world
      ??? // register with job queue
    }
  }
}

// State transitions
sealed abstract class PlacementState[T] {
  def assignWorker(workerID: Int): PlacementState[T] = {
    this match {
      case PlacementPending() => PlacementScheduled(workerID)
      case RemovalPending() => RemovalScheduled(workerID)
      case _ => throw new IllegalStateException(s"unexpected state $this")
    }
  }
  def jobFailed(): PlacementState[T] = {
    this match {
      case PlacementScheduled(worker) =>
        ??? // remove job from worker
        new Removed[T]
      case RemovalScheduled(worker) =>
        ??? // remove job from worker
        Placed()
      case PlacementPending() => Removed()
      case RemovalPending()   => Placed()
      case _ => throw new IllegalStateException(s"unexpected state $this")
    }
  }
  def jobDone(): PlacementState[T] = {
    this match {
      case PlacementPending() => Placed()
      case RemovalPending() => Removed()
      case PlacementScheduled(worker) =>
        ??? // remove job from worker
        Placed()
      case RemovalScheduled(worker) =>
        ??? // remove job from worker
        Removed()
    }
  }
  def requestRemoval()(location: Vector3f): PlacementState[T] = {
    val cheatMode = ???
    if (cheatMode) {
      ??? // RemovalPending.jobDone
    } else {
      ??? // register with job queue
      RemovalPending()
    }
  }
}


// States
case class Placed[T]() extends PlacementState[T]
case class PlacementPending[T]() extends PlacementState[T]
case class RemovalPending[T]() extends PlacementState[T]
case class PlacementScheduled[T](assignedWorkerID: Int) extends PlacementState[T]
case class RemovalScheduled[T](assignedWorkerID: Int) extends PlacementState[T]
case class Removed[T]() extends PlacementState[T]
