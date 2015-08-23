package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.material.Material
import com.jme3.math._
import com.jme3.scene._
import com.jme3.scene.shape.Box
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
  private val svo: SVO = SVO.size2
  private var svoRootNode: Node = _
  private lazy val boxMaterial = {
    val assetManager = app.getAssetManager
    val boxMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")
    boxMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/newspaper_diffuse.tga"))
    boxMaterial.setTexture("NormalMap", assetManager.loadTexture("Textures/newspaper_normal.tga"))
    boxMaterial.setBoolean("UseMaterialColors",true)
    boxMaterial.setColor("Diffuse",ColorRGBA.White)  // minimum material color
    boxMaterial.setColor("Specular",ColorRGBA.White) // for shininess
    boxMaterial.setColor("Ambient", ColorRGBA.White)
    boxMaterial.setFloat("Shininess", 64f) // [1,128] for shininess
    boxMaterial
  }

  // You can't make changes directly to the SVO or its geometry, you have to register your intention here.
  val insertionQueue = new mutable.Queue[(SVONode, Vector3f)]()

  // Depth 0 = full size, depth 1 = 1/2 size, depth 2 = 1/4 size etc.
  private def shinyBox(height: Int) = {
    val boxMesh = new Box(Vector3f.ZERO, Vector3f.UNIT_XYZ)
    val boxGeometry = new Geometry("Shiny box", boxMesh)
    TangentBinormalGenerator.generate(boxMesh)
    boxGeometry.setMaterial(boxMaterial)
    //println("scaling texture")
    //val scale = math.pow(2, height).toFloat
    //boxGeometry.getMesh.scaleTextureCoordinates(new Vector2f(scale, scale))
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
      svoRootNode.attachChild(child)
    }
  }

  override def update(tpf: Float): Unit = {
    super.update(tpf)

    // DO something with the queue here

    // TODO: this is pretty fucked
    // Will need to filter the queue for valid requests if we implement multiplayer.
    def svoInsertionToSpatialInsertion(svoNode: SVONode, position: Vector3f): Option[(Option[Spatial], List[Octant])] =
      for (refreshPath <- svo.insertNodeAt(svoNode, position, 0))
           //svoToRefresh <- svo.getNodePath(refreshPath))
        yield (Some(new Node()), refreshPath)

    val insertionList = insertionQueue.toList
    insertionQueue.clear()

    val geometryUpdateRequests: List[(Option[Spatial], List[Octant])] =
      (insertionList map Function.tupled(svoInsertionToSpatialInsertion)).flatten


    geometryUpdateRequests foreach { case (maybeNewGeometryasd, refreshPath) =>
      replaceGeometryPath(refreshPath)
    }
  }

  def replaceGeometryPath(path: List[Octant]): Unit = {
    Option(svoRootNode.getChild(FirstChildName)) match {
      case Some(topNode: Node) if path.nonEmpty =>
        replaceGeometryPathGo(topNode, List(), path)
      case _ =>
        svoRootNode.detachChildNamed(FirstChildName)
        createGeometryFromSVONode(svo.node, svo.height) map {spatial =>
          spatial.setName(FirstChildName)
          svoRootNode.attachChild(spatial)
        }
    }

    def replaceGeometryPathGo(parentNode: Node, reversedPathSoFar: List[Octant], pathRemaining: List[Octant]): Unit = pathRemaining match {
      // path cannot be empty
      case o :: os =>
        Option(parentNode.getChild(o.ix.toString)) match {
          // recurse
          case Some(childNode: Node) if os.nonEmpty =>
            replaceGeometryPathGo(childNode, o :: reversedPathSoFar, os)

          // draw from here
          case _ =>
            println("drawing here")
            println(s"pathSoFar: ${reversedPathSoFar.reverse}")
            println(s"path remaining: $pathRemaining")
            println()
            val svoNode = svo.getNodePath(reversedPathSoFar.reverse)
            val maybeNewGeometry = createGeometryFromSVONode(svoNode, pathRemaining.length)
            parentNode.detachChildNamed(o.ix.toString)
            maybeNewGeometry foreach {spatial =>
              spatial.setName(o.ix.toString)
              parentNode.attachChild(spatial)
            }

          }

    }

  /*
    // if list is empty or the top spatial is a geometry, replace it all
    // top spatial is a node, list is non-empty
      // if child exists: recurse
      //            else: rather than going further into the list, draw from here

  path match {
    case o::os
  }


  */ }

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
