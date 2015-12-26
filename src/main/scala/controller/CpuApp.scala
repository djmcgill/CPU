package controller

import com.jme3.app.SimpleApplication
import com.jme3.asset.plugins.FileLocator
import com.jme3.bullet.BulletAppState
import com.jme3.math.Vector3f
import controller.blockState.BlockManager
import controller.peonState.Peon
import controller.svoState.SvoManager
import controller.visualState.{OverviewCameraState, SkyboxState, LightingState}
import logic.voxels.SVO

class CpuApp extends SimpleApplication {
  def maxHeight: Int = rootNode.getUserData("maxHeight")
  def maxHeight_= (height: Int): Unit = rootNode.setUserData("maxHeight", height)

  def cheatMode: Boolean = rootNode.getUserData("cheatMode")
  def cheatMode_= (mode: Boolean): Unit = rootNode.setUserData("cheatMode", mode)

  def svo: SVO = rootNode.getUserData("svo")
  def svo_= (newSVO: SVO) = rootNode.setUserData("svo", newSVO)

  override def simpleInitApp() {
    maxHeight = 8
    cheatMode = true

    val bulletAppState = new BulletAppState()
    bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL)

    val svoWidth = math.pow(2, maxHeight).toFloat
    val peon1 = new Peon(0, new Vector3f(svoWidth/2, svoWidth/2 + 2, svoWidth/2))
    val peon2 = new Peon(1, new Vector3f(svoWidth/2 + 2, svoWidth/2 + 2, svoWidth/2))

    stateManager.attachAll(
      bulletAppState,
      new KeyBindings,
      new LightingState,
      new OverviewCameraState,
      new SvoManager,
      new SkyboxState,
      new BlockManager,
      peon1,
      peon2
    )

    assetManager.registerLocator("resources", classOf[FileLocator])
  }

}
