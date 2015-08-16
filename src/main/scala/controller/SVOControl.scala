package controller

import _root_.CPU._
import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, Vector2f, Vector3f}
import com.jme3.scene.{Node, Spatial, Geometry}
import com.jme3.scene.shape.Box
import com.jme3.util.TangentBinormalGenerator
import com.typesafe.scalalogging.LazyLogging
import controller.svoControl.{SVODeleteElementControl, SVOInsertElementControl}
import logic.voxels._

import scala.collection.mutable

/**
 * Renders a cube between 0,0,0 and 1,1,1
 * Will soon generate the physics controls too
 */
class SVOControl extends AbstractAppStateWithApp with LazyLogging {
  private val svo: SVO = SVO.initialWorld
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

  // You can't make changes directly to the SVO or it's geometry, you have to register your intention here.
  val insertionQueue = new mutable.Queue[(SVONode, Vector3f)]()

  // Depth 0 = full size, depth 1 = 1/2 size, depth 2 = 1/4 size etc.
  private def shinyBox(depth: Int) = {
    val boxMesh = new Box(Vector3f.ZERO, Vector3f.UNIT_XYZ)
    val boxGeometry = new Geometry("Shiny box", boxMesh)
    TangentBinormalGenerator.generate(boxMesh)
    boxGeometry.setMaterial(boxMaterial)
    val scale = math.pow(2, -depth).toFloat
    boxGeometry.getMesh.scaleTextureCoordinates(new Vector2f(scale, scale))

    // TODO: add physics
    //boxGeometry.addControl(new RigidBodyControl(0))
    boxGeometry
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getStateManager.attach(new SVOInsertElementControl(insertionQueue))
    app.getStateManager.attach(new SVODeleteElementControl(insertionQueue))
    app.getRootNode.setUserData("svo", svo)

    svoRootNode = new Node("SVO")
    createGeometryFromSVO(svo) foreach svoRootNode.attachChild
    app.getRootNode.attachChild(svoRootNode)
  }

  override def update(tpf: Float): Unit = {
    super.update(tpf)

    // Will need to filter the queue for valid requests if we implement multiplayer.
    for (
        (svoNode, position) <- insertionQueue;
        refreshPath         <- svo.insertNodeAt(svoNode, position,0)) {
      deleteFromGeometryPath(refreshPath)
      for (svoToRefresh <- svo.getNodePath(refreshPath);
           newGeometry  <- createGeometryFromSVO(svoToRefresh)) {
        insertIntoGeometryPath(newGeometry, refreshPath)
      }
    }
    insertionQueue.clear()
  }


  def insertIntoGeometryPath(geometry: Spatial, path: List[Octant]): Unit = ???
  def deleteFromGeometryPath(path: List[Octant]): Unit = ???

  def createGeometryFromSVO(svo: SVO): Option[Spatial] = ???


}
