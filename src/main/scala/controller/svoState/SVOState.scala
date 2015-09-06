package controller.svoState

import com.jme3.bullet.BulletAppState
import com.jme3.scene.Spatial
import controller.AbstractAppStateWithApp
import logic.voxels.SVO

/** A state that interacts with the SVO or its geometry. */
trait SVOState extends AbstractAppStateWithApp {
  lazy val svo: SVO = app.getRootNode.getUserData[SVO]("svo")
  def svoSpatial: Spatial = app.getRootNode.getChild("svoSpatial")
  lazy val spatialState: SVOSpatialState = app.getStateManager.getState[SVOSpatialState](classOf[SVOSpatialState])
  lazy val bulletAppState: BulletAppState = app.getStateManager.getState[BulletAppState](classOf[BulletAppState])
}
