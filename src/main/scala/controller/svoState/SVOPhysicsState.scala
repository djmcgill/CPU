package controller.svoState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes._
import com.jme3.bullet.control.{GhostControl, RigidBodyControl}
import com.jme3.bullet.objects.PhysicsGhostObject
import com.jme3.bullet.util.CollisionShapeFactory
import com.jme3.math.{Transform, Vector3f}
import com.jme3.scene._
import com.jme3.scene.shape.Box
import controller.GameState
import logic.voxels.{Octant, Subdivided, Full, SVO}

import scala.collection.JavaConversions._
import scala.concurrent.Promise

class SvoPhysicsState extends SvoState {
  private val SvoRootName = "svoSpatial"
  private lazy val bulletAppState: BulletAppState =
    app.getStateManager.getState[BulletAppState](classOf[BulletAppState])

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    Option(app.getRootNode.getChild(SvoRootName)) foreach attachSVOPhysics
  }

  // FIXME: this isn't working
  def svoSpatialCollidesWithEntity(height: Int, worldTranslation: Vector3f): Boolean = {
    println("WARNING: Tried to test for a collision in the world. This is not yet implemented")
    return false

    val extent = math.pow(2, height).toFloat
    val extents = new Vector3f(extent, extent, extent)
    val transform = new Transform(worldTranslation)
    val transform2 = new Transform(worldTranslation mult 1.1f)
    val shape: CollisionShape = new BoxCollisionShape(extents)
    val sweepResults = bulletAppState.getPhysicsSpace.sweepTest(shape, transform, transform2)
    !sweepResults.isEmpty
  }

  /** Recurse over a SVOSpatial, adding RigidBodyControls to each of the geometries.
    * Would using CompoundPhysicsShapes on the nodes be better?
    */

  def attachSVOPhysics(svoSpatial: Spatial): Unit = svoSpatial match {
    case cubeGeometry: Geometry =>
      if(!Option(cubeGeometry.getUserData[Boolean]("phantom")).contains(true)) {
        // Attach a cubic RigidBodyControl to this geometry.
        val physicsShape = new MeshCollisionShape(cubeGeometry.getMesh)
        physicsShape.setScale(cubeGeometry.getWorldScale)
        val physicsControl = new RigidBodyControl(physicsShape, 0)
        physicsControl.setKinematic(false)
        cubeGeometry.addControl(physicsControl)
        bulletAppState.getPhysicsSpace.add(physicsControl)
      }

    case cubeNode: Node =>
      // Recurse on all the sub-octants
      (0 until 8) foreach {ix => Option(cubeNode.getChild(ix.toString)) foreach attachSVOPhysics}
    case _ => throw new ClassCastException
  }

  /** Recurse over a SVOSpatial, removing RigidBodyControls from each of the sub-svo geometries. */
  def detachSVOPhysics(svoSpatial: Spatial): Unit = svoSpatial match {
    case cubeGeometry: Geometry =>
      if (cubeGeometry != null && svoSpatial.getControl[RigidBodyControl](classOf[RigidBodyControl]) != null) {
        bulletAppState.getPhysicsSpace.remove(cubeGeometry)
      }

    case cubeNode: Node =>
      // Recurse on all the sub-octants
      (0 until 8) foreach {ix => getImmediateChild(cubeNode, ix.toString) foreach detachSVOPhysics}
    case _ => throw new ClassCastException
  }

  private def getImmediateChild(node: Node, childName: String): Option[Spatial] = {
    node.getChildren.toList find (_.getName == childName)
  }
}
