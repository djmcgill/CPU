package controller.peonState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.control.BetterCharacterControl
import com.jme3.math.Vector3f
import com.jme3.scene.{Spatial, Node}
import controller.SvoState

case class Peon(id: Int, 
                startingPosition: Vector3f, 
                bulletAppState: BulletAppState, 
                jobManager: JobManager) extends SvoState {
  val peonScale = 0.5f
  var facingAngle = 0f
  
  lazy val node: Node = new Node(s"peon-$id")
  lazy val peonControl: BetterCharacterControl = new BetterCharacterControl(0.8f*peonScale, 4*peonScale, 1)
  lazy val jobSeekerControl = new PeonJobSeeker(this, jobManager, svoNavGrid)

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    // model is 4 units tall
    val peonSpatial = app.getAssetManager.loadModel("Models/StickFigure/Stick_Figure_by_Swp.OBJ")
    
    app.getRootNode.setUserData("peon-$id", node)
    node.attachChild(peonSpatial)
    peonSpatial.scale(peonScale)
    peonSpatial.move(0, 2*peonScale, 0)

    node.addControl(peonControl)
    peonControl.setJumpForce(new Vector3f(0,0.2f,0))
    peonControl.setGravity(new Vector3f(0,0.8f,0))
    peonControl.warp(startingPosition)
    bulletAppState.getPhysicsSpace.addAll(node)

    node.addControl(jobSeekerControl)
    app.getRootNode.attachChild(node)
  }

  def acceptableJob(unassignedJob: UnassignedJob): Boolean = true
  def assignJob(assignedJob: AssignedJob) = jobSeekerControl.currentJobs.enqueue(assignedJob)
}
