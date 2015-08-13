import com.jme3.app.SimpleApplication
import com.jme3.bullet.BulletAppState
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{ColorRGBA, Vector3f}
import controller.{SVODeleteElementControl, SVOInsertElementControl, OverviewCameraControls}
import jme3test.bullet.PhysicsTestHelper
import logic.voxels.SVO
import rendering.SVORenderer

object CPU extends SimpleApplication {
  var svo = SVO.initialWorld
  var svoRenderer: SVORenderer = _
  var bulletAppState: BulletAppState = _
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

    // TODO: turn the SVO into a collisionMesh. Again, ideally should only modify it as things change.
    bulletAppState = new BulletAppState()
    stateManager.attach(bulletAppState)
    //bulletAppState.getPhysicsSpace.addAll(svoNode)
    // CollisionShapeFactory

    val peons = new Peons(assetManager, bulletAppState, rootNode)
  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)

    bulletAppState.getPhysicsSpace
    rootNode.detachChildNamed("SVO")
    val svoNode = svoRenderer.node(svo)
    rootNode.attachChild(svoNode)
    // TODO: now refresh only the changes to the svo
    // TODO: update the physics space
  }
}