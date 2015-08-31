package controller.peonState

import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.control.{Control, AbstractControl}
import controller.peonState.jobs.PeonSimplePathfinding

class JobSeekerControl(jobQueueManager: PeonJobQueue) extends AbstractControl{

  override def controlRender(renderManager: RenderManager, viewPort: ViewPort): Unit = {}

  override def controlUpdate(v: Float): Unit = {
    if (spatial.getControl[PeonSimplePathfinding](classOf[PeonSimplePathfinding]) == null) {
      val newJob: Control = jobQueueManager.requestJob()
      spatial.addControl(newJob)
    }
  }
}
