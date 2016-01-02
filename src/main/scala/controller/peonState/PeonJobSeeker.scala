package controller.peonState


import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.control.AbstractControl
import logic.voxels.SvoNavGrid

import scala.collection.mutable

/** The spatial that this is attached to will perform jobs */
class PeonJobSeeker(peon: Peon,
                    jobManager: JobManager,
                    svoNavGrid: SvoNavGrid) extends AbstractControl {
  val currentJobs: mutable.Queue[AssignedJob] = mutable.Queue.empty

  override def controlRender(renderManager: RenderManager, viewPort: ViewPort): Unit = {}

  override def controlUpdate(v: Float): Unit = {
    if (currentJobs.isEmpty) jobManager.assignJob(peon)
    val currentJob = currentJobs.head
    val finished = currentJob.update(v)
    if (finished) {currentJobs.dequeue()}
  }
}
