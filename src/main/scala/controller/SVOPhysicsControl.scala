package controller.SVOPhysicsControl

import com.typesafe.scalalogging.LazyLogging
import controller.AbstractAppStateWithApp

class SVOPhysicsControl extends AbstractAppStateWithApp with LazyLogging {


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