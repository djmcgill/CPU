package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.material.Material
import com.jme3.math._
import com.jme3.scene._
import com.jme3.scene.shape.Box
import com.jme3.texture.Texture
import com.jme3.util.TangentBinormalGenerator
import controller.svoControl.{SVODeleteElementControl, SVOInsertElementControl}
import logic.voxels._

import scala.collection.mutable

/**
 * Renders a svo between (0,0,0) and (1,1,1)
 * Will soon generate the physics controls too
 */
class SVOGraphicsControl extends AbstractAppStateWithApp {
  private val FirstChildName = "First child"
  private val svo: SVO = SVO.size3
  private var svoRootNode: Node = _
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

    val scale = math.pow(2, height).toFloat
    println(s"shinyBox at size $height gave a scale of $scale")
    boxGeometry.getMesh.scaleTextureCoordinates(new Vector2f(scale, scale))

    boxGeometry
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getStateManager.attach(new SVOInsertElementControl(insertionQueue))
    app.getStateManager.attach(new SVODeleteElementControl(insertionQueue))
    app.getRootNode.setUserData("svo", svo)

    svoRootNode = new Node("SVO")
    app.getRootNode.attachChild(svoRootNode)
    createGeometryFromSVONode(svo.node, svo.height) foreach {child =>
      child.setName(FirstChildName)
      svoRootNode.attachChild(child)}
  }

  override def update(tpf: Float): Unit = {
    super.update(tpf)

    // Will need to filter the queue for valid requests if we implement multiplayer.
    insertionQueue foreach {case (svoNode, position) =>
      svo.insertNodeAt(svoNode, position, 0) foreach replaceGeometryPath}
    insertionQueue.clear()
  }

  def replaceGeometryPath(path: List[Octant]): Unit = {
    def replaceGeometryPathGo(spatialToModify: Option[Node], reversedPathSoFar: List[Octant], pathRemaining: List[Octant]): Option[Spatial] =
      (spatialToModify, pathRemaining) match {
        case (Some(nodeToModify: Node), o :: os) =>
          // Modify the node in-place
          Option(nodeToModify.getChild(o.ix.toString)) match {
            case Some(childNode: Node) =>
              // Recurse into the child node
              replaceGeometryPathGo(Some(childNode), o :: reversedPathSoFar, os) map {newChildSpatial: Spatial =>
                // We need to replace the child node
                nodeToModify.detachChildNamed(o.ix.toString)
                newChildSpatial.setName(o.ix.toString)
                newChildSpatial.setLocalTranslation(o.childOrigin mult 2)
                nodeToModify.attachChild(newChildSpatial)
                nodeToModify}

            case _ =>
              // Replace the child node
              val svoNodeToDraw = svo.getNodePath((o :: reversedPathSoFar).reverse)
              val nodeHeight = svo.height - reversedPathSoFar.length - 1
              val maybeNewChildSpatial = createGeometryFromSVONode(svoNodeToDraw, nodeHeight)
              nodeToModify.detachChildNamed(o.ix.toString)
              maybeNewChildSpatial foreach {newChildSpatial =>
                newChildSpatial.setLocalTranslation(o.childOrigin mult 2)
                newChildSpatial.setName(o.ix.toString)
                nodeToModify.attachChild(newChildSpatial)}
              None}

        case (_, Nil) =>
          // Replace with a new spatial here
          val svoNodeToDraw = svo.getNodePath(reversedPathSoFar.reverse)
          val nodeHeight = svo.height - reversedPathSoFar.length
          val maybeNewSpatial: Option[Spatial] = createGeometryFromSVONode(svoNodeToDraw, nodeHeight)
          maybeNewSpatial match {
            case Some(newSpatial) => Some(newSpatial)
            case None =>
              val newNode = new Node()
              newNode.scale(0.5f)
              Some(newNode)}

        case (_, o :: os) =>
          // Make a new node and recurse
          val newNode = new Node()
          newNode.scale(0.5f)
          val maybeNewChild = replaceGeometryPathGo(Some(newNode), o :: reversedPathSoFar, os)
          maybeNewChild foreach {newChild =>
            newChild.setName(o.ix.toString)
            newChild.setLocalTranslation(o.childOrigin mult 2)
            newNode.attachChild(newChild)}
          Some(newNode)}

    Option(svoRootNode.getChild(FirstChildName)) match {
      case Some(svoFirstNode: Node) =>
        val maybeNewNodeG = replaceGeometryPathGo(Some(svoFirstNode), List(), path)
        maybeNewNodeG foreach {newNode =>
          svoRootNode.detachChildNamed(FirstChildName)
          newNode.setName(FirstChildName)
          svoRootNode.attachChild(newNode)
        }
      case _ => throw new IllegalStateException("You deleted the world!!!")
    }
  }

  def createGeometryFromSVONode(svoNode: SVONode, svoHeight: Int): Option[Spatial] = svoNode match {
    case Full(None) => None

    case Full(_) =>
      Some(shinyBox(svoHeight).clone)

    case Subdivided(subSVOs) =>
      val subGeometries = subSVOs map (svo => createGeometryFromSVONode(svo.node, svo.height))
      val newNode = new Node()
      newNode.scale(0.5f)
      for (ix <- 0 until 8;
           newGeometry <- subGeometries(ix)) {
        newGeometry.setLocalTranslation(Octant(ix).childOrigin mult 2)
        newGeometry.setName(ix.toString)

        newNode.attachChild(newGeometry)
      }
      Some(newNode)
  }
}
