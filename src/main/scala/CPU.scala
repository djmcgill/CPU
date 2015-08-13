import com.jme3.app.SimpleApplication
import com.jme3.bullet.BulletAppState
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{ColorRGBA, Vector3f}
import controller.{SVODeleteElementControl, SVOInsertElementControl, OverviewCameraControls}
import logic.voxels.SVO
import rendering.SVORenderer

object CPU extends SimpleApplication {
  var svo = SVO.initialWorld
  var svoRenderer: SVORenderer = _
  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  override def simpleInitApp() {
    rootNode.setUserData("svo", svo)

    stateManager.attach(new BulletAppState)
    stateManager.attach(new OverviewCameraControls)
    stateManager.attach(new SVOInsertElementControl)
    stateManager.attach(new SVODeleteElementControl)
    svoRenderer = new SVORenderer(assetManager)

    val svoNode = svoRenderer.node(svo)
    rootNode.attachChild(svoNode)

    val sun = new DirectionalLight()
    sun.setDirection(new Vector3f(0,-1,-1).normalizeLocal())
    sun.setColor(ColorRGBA.White mult 1.5f)
    rootNode.addLight(sun)

    val ambient: AmbientLight = new AmbientLight()
    ambient.setColor(ColorRGBA.White)
    rootNode.addLight(ambient)
  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)

    rootNode.detachChildNamed("SVO")
    val svoNode = svoRenderer.node(svo)
    rootNode.attachChild(svoNode)
    // TODO: now refresh only the changes to the svo
  }
}