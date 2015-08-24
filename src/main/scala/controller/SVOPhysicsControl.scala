package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes._
import com.jme3.bullet.control.{RigidBodyControl, PhysicsControl}
import com.jme3.math.Vector3f
import com.jme3.scene._
import logic.voxels._

import scala.collection.mutable

class SVOPhysicsControl(bulletAppState: BulletAppState) extends AbstractAppStateWithApp {
  // TODO: it's not great having the physics queue and the graphics queue being different
  val insertionQueue = new mutable.Queue[(SVONode, Vector3f)]()
  private val FirstChildName = "First child"


  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    val svo = app.getRootNode.getUserData[SVO]("svo")
    Option(app.getRootNode.getChild(FirstChildName)) foreach {attachPhysics(_, svo.height)}
  }

  private def attachPhysics(svoSpatial: Spatial, height: Int): Unit = svoSpatial match {
    case cubeGeometry: Geometry =>
      val physicsShape = new MeshCollisionShape(cubeGeometry.getMesh)
      physicsShape.setScale(cubeGeometry.getWorldScale)
      val physicsControl = new RigidBodyControl(physicsShape, 0)
      physicsControl.setKinematic(false)
      cubeGeometry.addControl(physicsControl)
      bulletAppState.getPhysicsSpace.add(physicsControl)
      println(s"The cube with origin ${cubeGeometry.getWorldTranslation} and scale ${cubeGeometry.getWorldScale}" +
              s"now has a meshCollisionShape with origin ${physicsControl.getPhysicsLocation} and scale ${physicsControl.getCollisionShape.getScale}")
    case cubeNode: Node =>
      val possibleChildNames = Array("0", "1", "2", "3", "4", "5", "6", "7")
      possibleChildNames foreach {childName =>
        Option(cubeNode.getChild(childName)) foreach (attachPhysics(_, height - 1))}
    case _ => throw new ClassCastException
  }
}

// TODO: add physics with a MeshCollisionShape
// create a collision shape
// physics control = new (shape, 0)
// add control to spatial
// add control to physics space
// attach spatial to root node

// TODO: can only add physics AFTER full rendering



//control.setApplyPhysicsLocal()
//boxGeometry.addControl(new MeshCollisionShape(boxGeometry.getMesh))