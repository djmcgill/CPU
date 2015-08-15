package rendering

import com.jme3.app.{SimpleApplication, Application}
import com.jme3.asset.AssetManager
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.material.Material
import com.jme3.math.{Transform, Vector3f, ColorRGBA}
import com.jme3.scene.{Spatial, Geometry, Node}
import com.jme3.scene.shape.Box
import com.jme3.util.TangentBinormalGenerator
import logic.voxels._
import scala.collection.JavaConversions._


/**
 * Renders a cube between 0,0,0 and 1,1,1
 */
// TODO: this should for sure be a Control or something
class SVOGeometry(app: Application) {
  // TODO: tile the texture over full (non-size-0) cubes rather than stretch it
  // TODO: add physics to each box
  // TODO: share as much as possible of each shinyBox
  lazy val shinyBox = {
    val assetManager = app.getAssetManager
    val boxMesh = new Box(Vector3f.ZERO, Vector3f.UNIT_XYZ)
    val boxGeometry = new Geometry("Shiny box", boxMesh)
    TangentBinormalGenerator.generate(boxMesh)
    val boxMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")
    boxMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"))
    boxMaterial.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"))
    boxMaterial.setBoolean("UseMaterialColors",true)
    boxMaterial.setColor("Diffuse",ColorRGBA.White)  // minimum material color
    boxMaterial.setColor("Specular",ColorRGBA.White) // for shininess
    boxMaterial.setColor("Ambient", ColorRGBA.White)
    boxMaterial.setFloat("Shininess", 64f) // [1,128] for shininess


    boxGeometry.setMaterial(boxMaterial)

    // TODO:
    //boxGeometry.addControl(new RigidBodyControl(0))
    boxGeometry
  }

  def regenerateGeometry(node: Node, path: List[Octant]) = {
    val sApp = app match {case s: SimpleApplication => s}
    val svo = sApp.getRootNode.getUserData("svo")
    println(s"regenerating $path")
    regenerateGeometryGo(svo, node, path)
  }

  /** Regenerate the geometries for a particular subnode as specified by the path.
    * If path is empty than the whole tree geometry will be regenerated.
    */
  def regenerateGeometryGo(svo: SVO, node: Node, path: List[Octant]): Unit = path match {
    case o :: Nil =>
      val maybeNewSpatial = generateNode(svo.node match {case Subdivided(arr) => arr(o.ix)})
      node.detachChildNamed(o.ix.toString)
      maybeNewSpatial.setName(o.ix.toString)
      node.attachChild(maybeNewSpatial)


    case o :: os =>
      // WARNING: this will crash if the path terminates unexpectedly
      val childNode = node.getChild(o.ix.toString) match {case n: Node => n}
      val childSVO = svo.node match {case Subdivided(arr) => arr(o.ix)}
      regenerateGeometryGo(childSVO, childNode, os)

    case Nil =>
      ??? // ahhhh
  }

  def generateNode(svo: SVO): Node = {
    val node = new Node("SVO")
    generateSubNode(svo.node) foreach node.attachChild
    node
  }

  private def generateSubNode(svo: SVONode): Option[Spatial] = svo match {
    case Full(Some(_)) => Some(shinyBox.clone)
    case Full(None) => None
    case Subdivided(subNodes) =>
      val subMaybeNodes = subNodes map ((svo: SVO) => generateSubNode(svo.node))
      val node = new Node()

      subMaybeNodes.zipWithIndex foreach {case (optionSubNode: Option[Spatial], ix) =>
        for (subNode <- optionSubNode) {
          subNode.setName(ix.toString)
          val newOrigin: Vector3f = new Octant(ix).childOrigin
          subNode.setLocalTranslation(newOrigin)
          subNode.scale(0.5f)
          node.attachChild(subNode)
        }
      }
      Some(node)
  }
}
