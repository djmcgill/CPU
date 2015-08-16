import com.jme3.app.SimpleApplication
import com.jme3.bullet.BulletAppState
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{ColorRGBA, Vector3f}
import controller._

object CPU extends SimpleApplication {
  var bulletAppState: BulletAppState = _

  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  override def simpleInitApp() {
    stateManager.attachAll(
      new BulletAppState,
      new OverviewCameraControls,
      new SVOControl
    )

    // Lighting
    val sun = new DirectionalLight()
    sun.setDirection(new Vector3f(0,-1,-1).normalizeLocal())
    sun.setColor(ColorRGBA.White mult 1.5f)
    rootNode.addLight(sun)

    val ambient: AmbientLight = new AmbientLight()
    ambient.setColor(ColorRGBA.White)
    rootNode.addLight(ambient)

    // TODO: Entities
  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)
  }
}
