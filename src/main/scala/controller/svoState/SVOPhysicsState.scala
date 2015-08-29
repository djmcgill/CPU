package controller.svoState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes._
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.scene._
import controller.AbstractAppStateWithApp
import logic.voxels._

class SVOPhysicsState extends AbstractAppStateWithApp {
  private val FirstChildName = "First child"
  private var bulletAppState: BulletAppState = _

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    bulletAppState = app.getStateManager.getState[BulletAppState](classOf[BulletAppState])
    Option(app.getRootNode.getChild(FirstChildName)) foreach attachSVOPhysics
  }

  /** Recurse over a SVOSpatial, adding RigidBodyControls to each of the geometries.
    * Would using CompoundPhysicsShapes on the nodes be better?
    */
  def attachSVOPhysics(svoSpatial: Spatial): Unit = svoSpatial match {
    case cubeGeometry: Geometry =>
      // Attach a cubic RigidBodyControl to this geometry.
      val physicsShape = new MeshCollisionShape(cubeGeometry.getMesh)
      physicsShape.setScale(cubeGeometry.getWorldScale)
      val physicsControl = new RigidBodyControl(physicsShape, 0)
      physicsControl.setKinematic(false)
      cubeGeometry.addControl(physicsControl)
      bulletAppState.getPhysicsSpace.add(physicsControl)

    case cubeNode: Node =>
      // Recurse on all the sub-octants
      val possibleChildNames = Array("0", "1", "2", "3", "4", "5", "6", "7")
      possibleChildNames foreach {childName =>
        Option(cubeNode.getChild(childName)) foreach attachSVOPhysics}
    case _ => throw new ClassCastException
  }

  override def cleanup(): Unit = {
    Option(app.getRootNode.getChild(FirstChildName)) foreach detachSVOPhysics
    super.cleanup()
  }

  /** Recurse over a SVOSpatial, removing RigidBodyControls from each of the
    * sub-svo geometries.
    */
  def detachSVOPhysics(svoSpatial: Spatial): Unit = svoSpatial match {
    case cubeGeometry: Geometry =>
      // Detach a RigidBodyControl from this geometry.
      cubeGeometry.removeControl(classOf[RigidBodyControl])

    case cubeNode: Node =>
      // Recurse on all the sub-octants
      val possibleChildNames = Array("0", "1", "2", "3", "4", "5", "6", "7")
      possibleChildNames foreach {childName =>
        Option(cubeNode.getChild(childName)) foreach detachSVOPhysics}
    case _ => throw new ClassCastException
  }
}
