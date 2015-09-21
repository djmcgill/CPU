package controller.peonState

import com.jme3.math.Vector3f
import controller.AbstractAppStateWithApp

import scala.collection.mutable

class JobStateState extends AbstractAppStateWithApp {
  def requestInteractWithBlock(position: Vector3f): Unit = jobQueue.enqueue(InteractWithBlock(position))

  // TODO: have a positional element to this, so that the peon will get the nearest(ish) job
  private val jobQueue = mutable.Queue[JobState]()
  def peonRequestJob(peonId: Long): JobState =
    if (jobQueue.isEmpty) {Idle()} else {jobQueue.dequeue()}
}

// TODO: add a way to interrupt jobs
abstract sealed class JobState

// Go stand at a random location in the specified flat square.
case class Idle(
    idleLocationCenter: Vector3f = ???,
    idleLocationExtent: Float = ???) extends JobState

// Follow this path.
// TODO: add a way to specify an action once we get there.
case class InteractWithBlock(position: Vector3f) extends JobState

