package controller.peonState

import com.jme3.math.Vector3f
import controller.GameState

import scala.collection.mutable

class JobManager extends GameState {
  def requestInteractWithBlock(position: Vector3f): Unit = jobQueue.enqueue(InteractWithBlock(position))

  // TODO: have a positional element to this, so that the peon will get the nearest(ish) job
  private val jobQueue = mutable.Queue[JobState]()
  def peonRequestJob(peonId: Long): JobState =
    if (jobQueue.isEmpty) Idle() else jobQueue.dequeue()
}
