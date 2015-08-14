import com.jme3.app.SimpleApplication
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes.CollisionShape
import com.jme3.bullet.control.{PhysicsControl, RigidBodyControl}
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.scene.Node
import controller.{SVODeleteElementControl, SVOInsertElementControl, OverviewCameraControls}
import jme3test.bullet.PhysicsTestHelper
import logic.voxels.SVO
import rendering.SVOGeometry

object CPU extends SimpleApplication {
  var svo = SVO.initialWorld
  var svoGeometry: SVOGeometry = _
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
    svoGeometry = new SVOGeometry(assetManager)

    val svoNode = svoGeometry.node(svo)
    rootNode.attachChild(svoNode)
    bulletAppState = new BulletAppState()
    stateManager.attach(bulletAppState)

    val sun = new DirectionalLight()
    sun.setDirection(new Vector3f(0,-1,-1).normalizeLocal())
    sun.setColor(ColorRGBA.White mult 1.5f)
    rootNode.addLight(sun)

    val ambient: AmbientLight = new AmbientLight()
    ambient.setColor(ColorRGBA.White)
    rootNode.addLight(ambient)

    // TODO: turn the SVO into a collisionMesh. Again, ideally should only modify it as things change.

    //bulletAppState.getPhysicsSpace.enableDebug(assetManager)

    val peons = new Peons(assetManager, bulletAppState, rootNode)
    //val svoPhysicsControl: Option[PhysicsControl] = SVOPhysics.mesh(svo) map (new RigidBodyControl(_, 0))
    //svoPhysicsControl foreach (bulletAppState.getPhysicsSpace.add(_))
  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)



    val oldSVO: Node = rootNode.getChild("SVO") match {case n: Node => n}
    rootNode.detachChild(oldSVO)
    bulletAppState.getPhysicsSpace.remove(oldSVO)
    val svoNode = svoGeometry.node(svo)
    rootNode.attachChild(svoNode)
    bulletAppState.getPhysicsSpace.add(svoNode)
    // TODO: add the physics control to each spatial in the SVO. That way it can be more incremental
    // TODO: now refresh only the changes to the svo
    // TODO: update the physics space
  }
}