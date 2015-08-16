import com.jme3.app.SimpleApplication
import com.jme3.bullet.BulletAppState
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.scene.Node
import controller._
import controller.svoControl.{SVOInsertElementControl, SVODeleteElementControl}
import logic.voxels._

import scala.collection.mutable

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

    // Entities
    //val peons = new Peons(assetManager, bulletAppState, rootNode)
  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)
  }
}
