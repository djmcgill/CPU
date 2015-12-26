package controller

import com.jme3.app.SimpleApplication
import com.jme3.app.state.AppState
import com.jme3.asset.plugins.FileLocator
import com.jme3.bullet.BulletAppState
import com.jme3.math.Vector3f
import controller.peonState.{Peon, PeonManager}
import controller.svoState.SvoManager
import logic.voxels.SVO

import scala.collection.JavaConversions.asJavaIterable

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

    val svoWidth = math.pow(2, maxHeight).toFloat
    stateManager.attachAll(
      bulletAppState,
      new KeyBindings,
      new LightingState,
      new OverviewCameraState,
      new SvoManager,
      new SkyboxState,
      new BlockController,
      new Peon(0, new Vector3f(svoWidth/2, svoWidth/2 + 2, svoWidth/2)),
      new Peon(1, new Vector3f(svoWidth/2 + 2, svoWidth/2 + 2, svoWidth/2))
    )

    assetManager.registerLocator("resources", classOf[FileLocator])
  }

}
