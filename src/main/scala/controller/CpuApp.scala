package controller

import com.jme3.app.SimpleApplication
import com.jme3.app.state.AppState
import com.jme3.asset.plugins.FileLocator
import com.jme3.bullet.BulletAppState
import com.jme3.math.Vector3f
import controller.blockState.BlockManager
import controller.peonState.{PeonManager, Peon}
import controller.svoState.SvoManager
import controller.visualState.{OverviewCameraState, SkyboxState, LightingState}
import logic.voxels.{SvoNavGrid, SVO}

import scala.collection.JavaConversions._

class CpuApp extends SimpleApplication {
  def maxHeight: Int = rootNode.getUserData("maxHeight")
  def maxHeight_= (height: Int): Unit = rootNode.setUserData("maxHeight", height)

  def cheatMode: Boolean = rootNode.getUserData("cheatMode")
  def cheatMode_= (mode: Boolean): Unit = rootNode.setUserData("cheatMode", mode)

  def svo: SVO = rootNode.getUserData("svo")
  def svo_= (newSVO: SVO) = rootNode.setUserData("svo", newSVO)

  def svoNavGrid: SvoNavGrid = rootNode.getUserData("navGrid")
  def svoNavGrid_= (newSvoNavGrid: SvoNavGrid): Unit = rootNode.setUserData("navGrid", newSvoNavGrid)

  override def simpleInitApp() {
    maxHeight = 8
    cheatMode = false

    val bulletAppState = new BulletAppState()
    bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL)

    stateManager.attachAll(
      bulletAppState,
      new KeyBindings,
      new LightingState,
      new OverviewCameraState,
      new SvoManager,
      new SkyboxState,
      new BlockManager,
      new PeonManager(math.pow(2, maxHeight).toFloat)
    )

    assetManager.registerLocator("resources", classOf[FileLocator])
  }

}
