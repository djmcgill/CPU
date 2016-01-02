package controller.peonState

import com.jme3.math.Vector3f

class UnassignedJob(job: AssignedJob) {
  def assignTo(peon: Peon) = {
    job.peon = peon
    peon.assignJob(job)
  }
}

abstract sealed class AssignedJob {
  /** Perform a job and then indicate if the job is finished or not */
  var peon: Peon
  def update(dt: Float): Boolean
}

// Go stand at a random location in the specified flat circle.
case class Idle(override var peon: Peon = null) extends AssignedJob {
  var angle = 0.0f
  override def update(dt: Float): Boolean = {
    if (angle > 5f) true else {
      angle = angle + 5f * dt
      peon.node.rotate(0f, angle, 0f)
      false
    }
  }
}

// Follow this path.
// TODO: add a way to specify an action once we get there.
case class InteractWithBlock(position: Vector3f, override var peon: Peon = null) extends AssignedJob {
  val WalkSpeedMult = 3.0f

  override def update(dt: Float): Boolean = {
    peon.peonControl.setWalkDirection(Vector3f.UNIT_X mult WalkSpeedMult mult dt)
    false
  }

  private def actuallyWalkTowardsTarget(currentLocation: Vector3f, currentTarget: Vector3f): Unit = {
    val facingTarget = currentTarget
    facingTarget.y = currentLocation.y
    peon.node.lookAt (facingTarget, Vector3f.UNIT_Y)

    val walkDirection = currentTarget subtract currentLocation
    walkDirection.y = 0
    walkDirection.normalizeLocal ()
    peon.peonControl.jump()
    peon.peonControl.setWalkDirection(walkDirection mult WalkSpeedMult)
  }
}

