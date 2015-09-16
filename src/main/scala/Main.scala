import com.jme3.app.SimpleApplication
import com.jme3.asset.plugins.FileLocator
import com.jme3.bullet.BulletAppState
import com.jme3.texture.Texture
import com.jme3.util.SkyFactory
import controller._
import controller.peonState._
import controller.svoState.SVOSpatialState

object Main extends SimpleApplication {
  val MaxHeight = 8

  def main(args: Array[String]): Unit = {
    Main.start()
  }

  override def simpleInitApp() {
    rootNode.setUserData("maxHeight", MaxHeight)
    rootNode.setUserData("cheatMode", true)
    val bulletAppState = new BulletAppState
    bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL)

    stateManager.attachAll(
      bulletAppState,
      new KeyBindings,
      new LightingState,
      new OverviewCameraState,
      new SVOSpatialState,
      new Peon,
      //new SkyboxState, FIXME: apparently the skybox textures are not in git
      new BlockStateState)
    bulletAppState.getPhysicsSpace.setAccuracy(0.001f)
    assetManager.registerLocator("resources", classOf[FileLocator])
  }
}
