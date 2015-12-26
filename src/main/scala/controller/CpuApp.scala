package controller

import com.jme3.app.SimpleApplication
import com.jme3.asset.plugins.FileLocator
import com.jme3.bullet.BulletAppState
import controller.peonState.Peon
import controller.svoState.SvoManager
import logic.voxels.SVO

class CpuApp extends SimpleApplication {
  def maxHeight: Int = rootNode.getUserData("maxHeight")
  def maxHeight_= (height: Int): Unit = rootNode.setUserData("maxHeight", height)

  def cheatMode: Boolean = rootNode.getUserData("cheatMode")
  def cheatMode_= (mode: Boolean): Unit = rootNode.setUserData("cheatMode", mode)

  def svo2: SVO = rootNode.getUserData("svo")
  def svo2_= (newSVO: SVO) = rootNode.setUserData("svo", newSVO)

  override def simpleInitApp() {
    maxHeight = 8
    cheatMode = true

    val bulletAppState = new BulletAppState()
    bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL)

    stateManager.attachAll(
      bulletAppState,
      new KeyBindings,
      new LightingState,
      new OverviewCameraState,
      new SvoManager,
      new Peon(0),
      new SkyboxState,
      new BlockController)
    assetManager.registerLocator("resources", classOf[FileLocator])
  }

}
