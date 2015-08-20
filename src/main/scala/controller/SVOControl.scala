package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.material.Material
import com.jme3.math._
import com.jme3.scene._
import com.jme3.scene.shape.Box
import com.jme3.util.TangentBinormalGenerator
import com.typesafe.scalalogging.LazyLogging
import controller.svoControl.{SVODeleteElementControl, SVOInsertElementControl}
import logic.voxels._

import scala.collection.mutable

/**
 * Renders a svo between (0,0,0) and (1,1,1)
 * Will soon generate the physics controls too
 */
// FIXME: insertion works for minimalSubdivided, delete not showing
class SVOControl extends AbstractAppStateWithApp with LazyLogging {
  private val FirstChildName = "First child"
  private val svo: SVO = SVO.minimalSubdivided
  private var svoRootNode: Node = _
  private lazy val boxMaterial = {
    val assetManager = app.getAssetManager
    val boxMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")
    boxMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"))
    boxMaterial.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"))
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
    val scale = math.pow(2, height).toFloat
    boxGeometry.getMesh.scaleTextureCoordinates(new Vector2f(scale, scale))

    // TODO: add physics with a MeshCollisionShape
    //boxGeometry.addControl(new RigidBodyControl(0))
    boxGeometry
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getStateManager.attach(new SVOInsertElementControl(insertionQueue))
    app.getStateManager.attach(new SVODeleteElementControl(insertionQueue))
    app.getRootNode.setUserData("svo", svo)

    svoRootNode = new Node("SVO")
    app.getRootNode.attachChild(svoRootNode)
    createGeometryFromSVO(svo) foreach {child =>
      child.setName(FirstChildName)
      svoRootNode.attachChild(child)
    }
  }

  override def update(tpf: Float): Unit = {
    super.update(tpf)
    // Will need to filter the queue for valid requests if we implement multiplayer.

    lazy val box2 = shinyBox(0).clone
    box2.scale(0.5f)




    for (
        (svoNode, position) <- insertionQueue;
        refreshPath         <- svo.insertNodeAt(svoNode, position, 0);
        svoToRefresh        <- svo.getNodePath(refreshPath);
        newGeometry         <- createGeometryFromSVO(svoToRefresh)) {
      val scale: Float = math.pow(2, -refreshPath.length).toFloat
      newGeometry.scale(scale)
//      replaceGeometryPath(createGeometryFromSVO(svo), List())
//      replaceGeometryPath(Some(newGeometry), refreshPath)
        replaceGeometryPath(Some(box2), refreshPath)
      }
    insertionQueue.clear()
//    svoRootNode.forceRefresh(true, true, true)
  }


  // TODO: still not working
  def replaceGeometryPath(maybeSpatialToInsert: Option[Spatial], path: List[Octant]): Unit = {
    println(s"inserting $maybeSpatialToInsert at $path into $svo")
    def attachChild() = maybeSpatialToInsert foreach {spatial =>
      spatial.setName(FirstChildName)
      svoRootNode.attachChild(spatial)
    }

    if (path.isEmpty) {
      // An empty list: this is now the first child.
      svoRootNode.detachChildNamed(FirstChildName)
      attachChild()
    } else Option(svoRootNode.getChild(FirstChildName)) match {
      // In these two cases we replace at the top level
      case None => println("top none"); attachChild()
      case Some(geo: Geometry) =>
        println("top geo")
        svoRootNode.detachChildNamed(FirstChildName)
        attachChild()

      // Unlike the previous two cases, we keep the top node the same and
      // only edit part of it.
      case Some(node: Node) =>
        println("top node")
        replaceGeometryPathGo(node, path)
    }
    println()

    def replaceGeometryPathGo(parentNode: Node, path: List[Octant]): Unit = path match {
          // This list is guaranteed to be non-empty.
          case o :: os =>
            def attachChild() = maybeSpatialToInsert foreach { spatial =>
              spatial.setName(o.ix.toString)
              spatial.setLocalTranslation(o.childOrigin)
              //spatial.scale(0.5f)
              parentNode.attachChild(spatial)
            }

            Option(parentNode.getChild(o.ix.toString)) match {
              // for these two cases we'll insert here
              case Some(geo: Geometry) =>
                println(s"found geo at $path")
                parentNode.detachChildNamed(o.ix.toString)
                attachChild()
              case None =>
                println(s"found none at $path")
                attachChild()


              // If the parent is subdivided, then if we're supposed to insert
              // here we do so, else recurse.
              case Some(subdivided: Node) =>
                println(s"found subdivided at $path")
                if (os.isEmpty) {
                  parentNode.detachChildNamed(o.ix.toString)
                  attachChild()
                } else { replaceGeometryPathGo(subdivided, os) }
            }
    }
  }

  def createGeometryFromSVO(svo: SVO): Option[Spatial] = svo.node match {
    case Full(None) => None

    case Full(_) => Some(shinyBox(svo.height).clone)

    case Subdivided(subSVOs) =>
      val subGeometries = subSVOs map createGeometryFromSVO
      val newNode = new Node()
      for (ix <- 0 until 8;
           newGeometry <- subGeometries(ix)) {
        newGeometry.setLocalTranslation(Octant(ix).childOrigin)
        newGeometry.scale(0.5f)
        newGeometry.setName(ix.toString)

        newNode.attachChild(newGeometry)
      }
      Some(newNode)
  }
}
