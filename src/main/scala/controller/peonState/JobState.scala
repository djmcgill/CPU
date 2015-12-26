package controller.peonState

import com.jme3.math.Vector3f

// TODO: add a way to interrupt jobs
abstract sealed class JobState

// Go stand at a random location in the specified flat circle.
case class Idle() extends JobState

// Follow this path.
// TODO: add a way to specify an action once we get there.
case class InteractWithBlock(position: Vector3f) extends JobState

