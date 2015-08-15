import com.jme3.app.SimpleApplication
import com.jme3.bullet.BulletAppState
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.scene.Node
import controller._
import logic.voxels._
import rendering.SVOGeometry

import scala.collection.mutable

object CPU extends SimpleApplication {
  // TODO: these should be in SVOManager class
  var svo = SVO.minimalSubdivided
  var svoNode: Node = _
  var svoGeometry: SVOGeometry = _


  var bulletAppState: BulletAppState = _

  // You can't make changes directly to the SVO or it's geometry, you have to register your intention here.
  val insertionQueue = new mutable.Queue[(SVONode, List[Octant])]()
  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  override def simpleInitApp() {


    bulletAppState = new BulletAppState() // save in the stateManager instead?
    stateManager.attach(bulletAppState)
    stateManager.attach(new OverviewCameraControls)
    stateManager.attach(new SVOInsertElementControl(insertionQueue))
    stateManager.attach(new SVODeleteElementControl(insertionQueue))

    // TODO: have some SVOManager class
    rootNode.setUserData("svo", svo)
    svoGeometry = new SVOGeometry(this)

    svoNode = svoGeometry.generateNode(svo)
    rootNode.attachChild(svoNode)

    // Lighting
    val sun = new DirectionalLight()
    sun.setDirection(new Vector3f(0,-1,-1).normalizeLocal())
    sun.setColor(ColorRGBA.White mult 1.5f)
    rootNode.addLight(sun)

    val ambient: AmbientLight = new AmbientLight()
    ambient.setColor(ColorRGBA.White)
    rootNode.addLight(ambient)

    val peons = new Peons(assetManager, bulletAppState, rootNode)
  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)

    val size = insertionQueue.size
    if (size != 0) println(size)
    val f: ((SVONode, List[Octant]) => Unit) = {case (node, path) =>
      println(s"about to regenerate $path")
      // modify svo and svoNode
      val maybeToRefresh = svo.insertNodePath(node, path)

      maybeToRefresh foreach { refreshPath =>
        if (refreshPath.isEmpty) {
          // We need to generate the whole thing again.
          svoNode = svoGeometry.generateNode(svo)
        } else {
          svoGeometry.regenerateGeometry(svoNode, refreshPath)
        }
      }
    }

    // TODO: move this code into a SVOManager control
    insertionQueue foreach {case (svoNode: SVONode, path: List[Octant]) =>
      println(s"TODO: actually insert $svoNode into the svo at $path now")}
    insertionQueue.clear()

  }
}