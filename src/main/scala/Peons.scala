import com.jme3.animation.AnimControl
import com.jme3.asset.AssetManager
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.control.BetterCharacterControl
import com.jme3.math.Vector3f
import com.jme3.scene.{Spatial, Node}

class Peons(assetManger: AssetManager, bulletAppState: BulletAppState, rootNode: Node) {
  // TODO: make this a CustomControl or AppState or something.

  val peonSpatial: Spatial = assetManger.loadModel("Models/Oto/Oto.mesh.xml")
  val peonNode = new Node()
  peonNode.attachChild(peonSpatial)
  peonSpatial.move(0, 0.1f, 0)
  peonSpatial.scale(0.1f)


  val peonControl = new BetterCharacterControl(0.1f, 0.1f, 1f)
  peonNode.addControl(peonControl)

  peonControl.setJumpForce(new Vector3f(0,5f,0))
  peonControl.setGravity(new Vector3f(0,1f,0))
  peonControl.warp(new Vector3f(0.5f,1.5f,0.5f)); // warp character into landscape at particular location

  // add to physics state
  bulletAppState.getPhysicsSpace.add(peonControl)
  bulletAppState.getPhysicsSpace.addAll(peonNode)
  rootNode.attachChild(peonNode); // add wrapper to root


}
