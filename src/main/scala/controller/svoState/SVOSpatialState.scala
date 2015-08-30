package controller.svoState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.BulletAppState
import com.jme3.material.Material
import com.jme3.math._
import com.jme3.scene._
import com.jme3.scene.shape.Box
import com.jme3.texture.Texture
import com.jme3.util.TangentBinormalGenerator
import controller.AbstractAppStateWithApp
import logic.voxels._

import scala.collection.mutable

import scala.collection.JavaConversions._

/**
 * Renders a svo and attaches the physics.
 */
// TODO: this should attach svophysics itself, rather than main doing it.
class SVOSpatialState extends AbstractAppStateWithApp {
  // TODO: switch these to lazy vals
  private val SvoRootName = "svoSpatial"
  private var svo: SVO = _
  private var bulletAppState: BulletAppState = _
  private var svoPhysicsState: SVOPhysicsState = _

  private lazy val boxMaterial = {
    val assetManager = app.getAssetManager
    val boxMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")

    val diffuseTexture = assetManager.loadTexture("Textures/newspaper_diffuse.tga")
    diffuseTexture.setWrap(Texture.WrapMode.Repeat)
    boxMaterial.setTexture("DiffuseMap", diffuseTexture)

    val normalTexture = assetManager.loadTexture("Textures/newspaper_normal.tga")
    normalTexture.setWrap(Texture.WrapMode.Repeat)
    boxMaterial.setTexture("NormalMap", normalTexture)

    boxMaterial.setBoolean("UseMaterialColors",true)
    boxMaterial.setColor("Diffuse",ColorRGBA.White)  // minimum material color
    boxMaterial.setColor("Specular",ColorRGBA.White) // for shininess
    boxMaterial.setColor("Ambient", ColorRGBA.White)
    boxMaterial.setFloat("Shininess", 64f) // [1,128] for shininess
    boxMaterial
  }

  // You can't make changes directly to the SVO or its geometry, you have to register your intention here.
  val insertionQueue = new mutable.Queue[(SVONode, Vector3f)]()

  private def shinyBox(height: Int) = {
    val boxMesh = new Box(Vector3f.ZERO, Vector3f.UNIT_XYZ)
    val boxGeometry = new Geometry("Shiny box", boxMesh)
    TangentBinormalGenerator.generate(boxMesh)
    boxGeometry.setMaterial(boxMaterial)
    val textureScale = math.pow(2, height).toFloat
    boxGeometry.getMesh.scaleTextureCoordinates(new Vector2f(textureScale, textureScale))
    boxGeometry.setUserData("height", height)
    boxGeometry
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)

    val maxHeight = app.getRootNode.getUserData[Int]("maxHeight")
    svo = SVO.initialWorld(maxHeight)
    app.getRootNode.setUserData("svo", svo)
    bulletAppState = app.getStateManager.getState[BulletAppState](classOf[BulletAppState])

    app.getStateManager.attach(new SVOInsertElementControl(insertionQueue))
    app.getStateManager.attach(new SVODeleteElementControl(insertionQueue))

    svoPhysicsState = new SVOPhysicsState
    app.getStateManager.attach(svoPhysicsState)

    // Create a spatial for the SVO and call it "svoSpatial".
    createSpatialFromSVONode(svo.node, svo.height) foreach {svoSpatial =>
      svoSpatial.setName(SvoRootName)
      app.getRootNode.attachChild(svoSpatial)
      val scale = math.pow(2, maxHeight).toFloat
      svoSpatial.scale(scale)
    }
  }

  override def update(tpf: Float): Unit = {
    super.update(tpf)

    // Will need to filter the queue for valid requests if we implement multiplayer.
    insertionQueue foreach { case (svoNode, position) =>
      svo.insertNodeAt(svoNode, position, 0) foreach replaceGeometryPath
    }
    insertionQueue.clear()
  }

  private def replaceGeometryPath(path: List[Octant]): Unit = {
    val svoSpatial = app.getRootNode.getChild(SvoRootName)
    if (svoSpatial == null) {throw new IllegalStateException("You deleted the world!!")}

    val svoToInsert: SVO = svo.getSVOPath(path)
    assert(svoToInsert.height + path.length == svo.height)

    val oldChild: Spatial = getSpatialAtAbsolute(svoSpatial, path)
    assert(oldChild.getUserData[Int]("height") == svoToInsert.height)

    val parentSpatial = oldChild.getParent
    svoPhysicsState.detachSVOPhysics(oldChild)
    parentSpatial.detachChild(oldChild)

    // Add the new spatial if it was generated.
    createSpatialFromSVONode(svoToInsert.node, svoToInsert.height) foreach { newChild =>
      newChild.setName(oldChild.getName)
      newChild.setLocalTranslation(oldChild.getLocalTranslation)
      parentSpatial.attachChild(newChild)
      svoPhysicsState.attachSVOPhysics(newChild)

    }

  }

  /** Turn a SVONode into a Spatial. */
  private def createSpatialFromSVONode(svoNode: SVONode, svoHeight: Int): Option[Spatial] = svoNode match {
    case Full(None) => None

    // Create a cube geometry here
    case Full(_) =>
      Some(shinyBox(svoHeight).clone)

    // Create a new node which contains the spatials of the sub-SVOs
    case Subdivided(subSVOs) =>
      val subSpatials = subSVOs map {svo =>
        createSpatialFromSVONode(svo.node, svo.height)}
      val newNode = new Node()
      newNode.setUserData("height", svoHeight)
      newNode.scale(0.5f)
      for (ix <- 0 until 8;
           subSpatial <- subSpatials(ix)) {
        subSpatial.setLocalTranslation(Octant(ix).childOrigin mult 2)
        subSpatial.setName(ix.toString)
        newNode.attachChild(subSpatial)}
      Some(newNode)
  }

  /** Go down a path until the spatial is returned. Will create child nodes if necessary
    * but will NOT split a geometry.
    */
  private def getSpatialAtAbsolute(svoSpatial: Spatial, path: List[Octant]): Spatial = {
    val height = svoSpatial.getUserData[Int]("height")
    (svoSpatial, path) match {
      case (geo: Geometry, Nil) => geo
      case (geo: Geometry, o :: os) =>
        val parent = geo.getParent
        val newChildNode = new Node(o.ix.toString)
        newChildNode.scale(0.5f)
        val parentHeight = parent.getUserData[Int]("height")
        newChildNode.setUserData("height", parentHeight - 1)
        newChildNode.setLocalTranslation(geo.getLocalTranslation)
        parent.detachChild(geo)
        parent.attachChild(newChildNode)
        getSpatialAtAbsolute(newChildNode, os)
        newChildNode

      case (node: Node, Nil) => node
      case (node: Node, o :: os) => getImmediateChild(node, o.ix.toString) match {
        case Some(childNode) =>
          val nodeHeight = node.getUserData[Int]("height")
          val childHeight = childNode.getUserData[Int]("height")
          if (nodeHeight != childHeight + 1) {
            println("There was a mismatch!")
            println(s"The parent $node has a height of $nodeHeight")
            println(s"but it's child $childNode has a height of $childHeight!")
          }
          getSpatialAtAbsolute(childNode, os)
        case None =>
          val newChildNode = new Node(o.ix.toString)
          newChildNode.scale(0.5f)
          val parentHeight = node.getUserData[Int]("height")
          newChildNode.setUserData("height", parentHeight - 1)
          newChildNode.setLocalTranslation(o.childOrigin mult 2)
          node.attachChild(newChildNode)
          getSpatialAtAbsolute(newChildNode, os)
      }
    }
  }

  override def cleanup(): Unit = {
    app.getStateManager.detach(svoPhysicsState)
    super.cleanup()
  }

  private def getImmediateChild(node: Node, childName: String): Option[Spatial] = {
    node.getChildren.toList find (_.getName == childName)
  }
}
