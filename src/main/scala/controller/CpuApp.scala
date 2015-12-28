package controller

import com.jme3.app.SimpleApplication
import com.jme3.app.state.AppState
import com.jme3.asset.plugins.FileLocator
import com.jme3.bullet.BulletAppState
import com.jme3.math.Vector3f
import controller.blockState.BlockManager
import controller.peonState.{JobManager, PeonManager, Peon}
import controller.svoState.SvoManager
import controller.visualState.{OverviewCameraState, SkyboxState, LightingState}
import logic.voxels.{SvoNavGrid, SVO}

import scala.collection.JavaConversions._

class CpuApp extends SimpleApplication {
  def cheatMode: Boolean = rootNode.getUserData("cheatMode")
  def cheatMode_= (mode: Boolean): Unit = rootNode.setUserData("cheatMode", mode)

  override def simpleInitApp() {
    val MaxHeight = 8
    cheatMode = false

    val bulletAppState = new BulletAppState()
    bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL)
    val jobManager = new JobManager()

    stateManager.attachAll(
      bulletAppState,
      new KeyBindings,
      new LightingState,
      new OverviewCameraState(MaxHeight),
      new SvoManager(MaxHeight),
      new SkyboxState,
      new BlockManager,
      jobManager,
      new PeonManager(math.pow(2, MaxHeight).toFloat, bulletAppState, jobManager)
    )

    assetManager.registerLocator("resources", classOf[FileLocator])
  }

}
