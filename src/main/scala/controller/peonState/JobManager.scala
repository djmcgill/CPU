package controller.peonState

import com.jme3.math.Vector3f
import controller.GameState

import scala.collection.mutable

class JobManager extends GameState {
  private val jobQueue = mutable.Queue[UnassignedJob]()

  def requestInteractWithBlock(position: Vector3f): Unit =
    jobQueue.enqueue(new UnassignedJob(InteractWithBlock(null, position)))

  def assignJob(peon: Peon): Unit = {
    println(s"peon ${peon.id} is requesting a job")
    val unassignedJob = jobQueue.dequeueFirst(peon.acceptableJob) getOrElse new UnassignedJob(Idle(null))
    unassignedJob.assignTo(peon)
  }
}
