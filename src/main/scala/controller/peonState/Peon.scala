package controller.peonState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.control.BetterCharacterControl
import com.jme3.math.Vector3f
import com.jme3.scene.Node
import controller.SvoState

class Peon(id: Int, startingPosition: Vector3f) extends SvoState {
  val peonScale = 0.5f
  var facingAngle = 0f

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    // model is 4 units tall
    val peonSpatial = app.getAssetManager.loadModel("Models/StickFigure/Stick_Figure_by_Swp.OBJ")
    val peonNode = new Node(s"peon-$id")
    app.getRootNode.setUserData("peon-$id", peonNode)
    peonNode.attachChild(peonSpatial)
    peonSpatial.scale(peonScale)
    peonSpatial.move(0, 2*peonScale, 0)

    val peonControl = new BetterCharacterControl(0.8f*peonScale, 4*peonScale, 1)
    peonNode.addControl(peonControl)
    peonControl.setJumpForce(new Vector3f(0,0.2f,0))
    peonControl.setGravity(new Vector3f(0,0.8f,0))
    peonControl.warp(startingPosition)

    val bulletAppState = app.getStateManager.getState(classOf[BulletAppState])
    bulletAppState.getPhysicsSpace.add(peonControl)
    bulletAppState.getPhysicsSpace.addAll(peonNode)

    val jobStateState = app.getStateManager.getState[JobManager](classOf[JobManager])
    val jobSeeker = new PeonJobSeeker(0, jobStateState, svoNavGrid)
    peonNode.addControl(jobSeeker)
    app.getRootNode.attachChild(peonNode)
  }
}
