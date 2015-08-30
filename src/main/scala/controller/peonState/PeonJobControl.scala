package controller.peonState

import com.jme3.input.KeyInput
import com.jme3.input.controls.{KeyTrigger, Trigger}
import com.jme3.math.Vector3f
import com.jme3.scene.Spatial
import controller.AbstractActionListenerState
import logic.voxels.{RayCaster, SVO}

import scala.concurrent.Promise

class PeonJobControl(peonNode: Spatial) extends AbstractActionListenerState {
  override val triggers: Seq[Trigger] = Seq(new KeyTrigger(KeyInput.KEY_Q))

  /** The actual action to perform */
  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    // find the target from the mouse
    if (!isPressed) {return}
    if(peonNode.getControl[PeonSimplePathfinding](classOf[PeonSimplePathfinding]) != null) {
      println("peon was already assigned a job!")
      return
    }
    val svo: SVO = app.getRootNode.getUserData[SVO]("svo")
    val rayOrigin = app.getCamera.getLocation
    val click2d = app.getInputManager.getCursorPosition
    def worldCoordsAtZ(z: Float) = app.getCamera.getWorldCoordinates(click2d, z)
    val rayDirection = (worldCoordsAtZ(1) subtractLocal worldCoordsAtZ(0)).normalizeLocal
    val result = RayCaster.cast(rayOrigin, rayDirection, svo)

    result foreach { case (absoluteHitPosition, _) =>

      // create a pathfinding job
      val success = Promise.apply[Boolean]()
      val pathfindingJob = new PeonSimplePathfinding(absoluteHitPosition, success, Some(300))

      // assign to the peon
      peonNode.addControl(pathfindingJob)
    }

    // TODO: do something on success
  }

  override val name: String = "SetPeonTarget"
}
