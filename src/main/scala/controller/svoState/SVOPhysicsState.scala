package controller.svoState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes._
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.scene._
import controller.AbstractAppStateWithApp

import scala.collection.JavaConversions._

class SVOPhysicsState extends AbstractAppStateWithApp {
  private val SvoRootName = "svoSpatial"
  private lazy val bulletAppState: BulletAppState =
    app.getStateManager.getState[BulletAppState](classOf[BulletAppState])

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    Option(app.getRootNode.getChild(SvoRootName)) foreach attachSVOPhysics
  }

  /**
    * Recurse over a SVOSpatial, adding RigidBodyControls to each of the geometries.
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
      (0 until 8) foreach {ix => Option(cubeNode.getChild(ix.toString)) foreach attachSVOPhysics}
    case _ => throw new ClassCastException
  }

  /** Recurse over a SVOSpatial, removing RigidBodyControls from each of the sub-svo geometries. */
  def detachSVOPhysics(svoSpatial: Spatial): Unit = svoSpatial match {
    case cubeGeometry: Geometry =>
      bulletAppState.getPhysicsSpace.remove(cubeGeometry)

    case cubeNode: Node =>
      // Recurse on all the sub-octants
      (0 until 8) foreach {ix => getImmediateChild(cubeNode, ix.toString) foreach detachSVOPhysics}
    case _ => throw new ClassCastException
  }

  private def getImmediateChild(node: Node, childName: String): Option[Spatial] = {
    node.getChildren.toList find (_.getName == childName)
  }

  override def cleanup(): Unit = {
    Option(app.getRootNode.getChild(SvoRootName)) foreach detachSVOPhysics
    super.cleanup()
  }
}
