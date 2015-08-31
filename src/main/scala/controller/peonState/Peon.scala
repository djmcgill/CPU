package controller.peonState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.control.BetterCharacterControl
import com.jme3.math.Vector3f
import com.jme3.scene.Node
import controller.AbstractAppStateWithApp


class Peon extends AbstractAppStateWithApp {
  val peonScale = 0.5f
  var facingAngle = 0f

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    // model is 4 units tall
    val peonSpatial = app.getAssetManager.loadModel("Models/StickFigure/Stick_Figure_by_Swp.OBJ")
    val peonNode = new Node("peon")
    app.getRootNode.setUserData("peon", peonNode)

    peonNode.attachChild(peonSpatial)

    peonSpatial.scale(peonScale)
    peonSpatial.move(0, 2*peonScale, 0)

    val peonControl = new BetterCharacterControl(0.8f*peonScale, 4*peonScale, 1)
    peonNode.addControl(peonControl)
    peonControl.setJumpForce(new Vector3f(0,0.2f,0))
    peonControl.setGravity(new Vector3f(0,0.8f,0))
    val maxHeight = app.getRootNode.getUserData[Int]("maxHeight")
    val scale = math.pow(2, maxHeight).toFloat
    peonControl.warp(new Vector3f(scale * 0.6f, scale, scale * 0.6f))

    val bulletAppState = app.getStateManager.getState[BulletAppState](classOf[BulletAppState])
    bulletAppState.getPhysicsSpace.add(peonControl)
    bulletAppState.getPhysicsSpace.addAll(peonNode)

    app.getRootNode.attachChild(peonNode)

    val jobQueue = app.getStateManager.getState[PeonJobQueue](classOf[PeonJobQueue])
    peonNode.addControl(new JobSeekerControl(jobQueue))
  }

  override def cleanup(): Unit = {
    super.cleanup()
  }
}
