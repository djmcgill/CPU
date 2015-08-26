package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes._
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.scene._
import logic.voxels._

class SVOPhysicsControl extends AbstractAppStateWithApp {
  private val FirstChildName = "First child"
  private var bulletAppState: BulletAppState = _

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    bulletAppState = app.getStateManager.getState[BulletAppState](classOf[BulletAppState])
    Option(app.getRootNode.getChild(FirstChildName)) foreach attachPhysics
  }

  private def attachPhysicsToGeometryVoxel(cubeGeometry: Geometry) = {
    val physicsShape = new MeshCollisionShape(cubeGeometry.getMesh)
    physicsShape.setScale(cubeGeometry.getWorldScale)
    val physicsControl = new RigidBodyControl(physicsShape, 0)
    physicsControl.setKinematic(false)
    cubeGeometry.addControl(physicsControl)
    bulletAppState.getPhysicsSpace.add(physicsControl)
  }

  def detachPhysicsFromSVOSpatial(svoSpatial: Spatial) = {
    svoSpatial match {
      case geo: Geometry => geo.removeControl(classOf[RigidBodyControl])
      case _ => }
    bulletAppState.getPhysicsSpace.removeAll(svoSpatial)
  }

  def attachPhysics(svoSpatial: Spatial): Unit = svoSpatial match {
    case cubeGeometry: Geometry =>
      attachPhysicsToGeometryVoxel(cubeGeometry)
    case cubeNode: Node =>
      val possibleChildNames = Array("0", "1", "2", "3", "4", "5", "6", "7")
      possibleChildNames foreach {childName =>
        Option(cubeNode.getChild(childName)) foreach attachPhysics}
    case _ => throw new ClassCastException
  }

  def addVoxelPhysicsPath(svoGeometry: Spatial, path: List[Octant]): Unit = (svoGeometry, path) match {
    case (voxelGeom: Geometry, _) => attachPhysicsToGeometryVoxel(voxelGeom)
    case (svoNode: Node,     Nil) =>
      detachPhysicsFromSVOSpatial(svoNode)
      attachPhysics(svoNode)
    case (svoNode: Node, o :: os) => Option(svoNode.getChild(o.ix.toString)) match {
      case Some(spatial: Spatial) => addVoxelPhysicsPath(spatial, os)
      case _ =>
    }
    case _ => throw new IllegalStateException
  }
}
