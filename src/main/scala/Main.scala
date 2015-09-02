import com.jme3.app.SimpleApplication
import com.jme3.asset.plugins.FileLocator
import com.jme3.bullet.BulletAppState
import controller._
import controller.peonState._
import controller.svoState.{SVOCuboidSelectionState, SVOSpatialState}

object Main extends SimpleApplication {
  val MaxHeight = 5

  def main(args: Array[String]): Unit = {
    Main.start()
  }

  override def simpleInitApp() {
    rootNode.setUserData("maxHeight", MaxHeight)
    val bulletAppState = new BulletAppState
    bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL)

    stateManager.attachAll(
      bulletAppState,
      new LightingState,
      new OverviewCameraState,
      new SVOSpatialState,
      new PeonJobQueue,
      new Peon,
      new SVOCuboidSelectionState
    )
    bulletAppState.getPhysicsSpace.setAccuracy(0.001f)
    assetManager.registerLocator("resources", classOf[FileLocator])
  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)
  }
}
