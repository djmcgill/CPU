package controller.peonState

import com.jme3.math.Vector3f
import controller.GameState

import scala.collection.mutable

class JobManager extends GameState {
  private val jobQueue = mutable.Queue[UnassignedJob]()

  def requestInteractWithBlock(position: Vector3f): Unit =
    jobQueue.enqueue(UnassignedJob(peon => InteractWithBlock(peon, position)))

  def assignJob(peon: Peon): Unit = {
    println(s"peon ${peon.id} is requesting a job")
    val unassignedJob = jobQueue.dequeueFirst(peon.acceptableJob) getOrElse UnassignedJob(peon => Idle(peon))
    val assignedJob = unassignedJob.assignJob(peon)
    peon.assignJob(assignedJob)
  }
}
