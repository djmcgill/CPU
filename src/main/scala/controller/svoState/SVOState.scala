package controller.svoState

import com.jme3.app.SimpleApplication
import com.jme3.bullet.BulletAppState
import com.jme3.scene.Spatial
import logic.voxels.SVO

/** A state that interacts with the SVO or its geometry. */
class SVOState(app: SimpleApplication) {


  lazy val svo: SVO = app.getRootNode.getUserData[SVO]("svo")
  lazy val svoSpatial: Spatial = app.getRootNode.getChild("svoSpatial")
  lazy val spatialState: SVOSpatialState = app.getStateManager.getState[SVOSpatialState](classOf[SVOSpatialState])
  lazy val bulletAppState: BulletAppState = app.getStateManager.getState[BulletAppState](classOf[BulletAppState])
}

