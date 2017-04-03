package controller.peonState

import controller.GameState
import controller.blockState.PlacementPending

import scala.collection.mutable

class JobManager extends GameState {
  private val jobQueue = mutable.Queue[UnassignedJob]()

  def requestPlaceBlock(block: PlacementPending): Unit =
    jobQueue.enqueue(new UnassignedJob(PlaceBlock(block)))

  def assignJob(peon: Peon): Unit = {
    println(s"peon ${peon.id} is requesting a job")
    val unassignedJob = jobQueue.dequeueFirst(peon.acceptableJob) getOrElse new UnassignedJob(Idle())
    unassignedJob.assignTo(peon)
  }
}
