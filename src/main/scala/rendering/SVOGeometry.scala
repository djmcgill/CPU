package rendering

import com.jme3.asset.AssetManager
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.material.Material
import com.jme3.math.{Transform, Vector3f, ColorRGBA}
import com.jme3.scene.{Spatial, Geometry, Node}
import com.jme3.scene.shape.Box
import com.jme3.util.TangentBinormalGenerator
import logic.voxels._


/**
 * Renders a cube between 0,0,0 and 1,1,1
 */
// TODO: according to the wiki, passing around AssetManagers is a bad idea.
class SVOGeometry(assetManager: AssetManager) {
  // TODO: tile the texture over full (non-size-0) cubes rather than stretch it
  // TODO: add physics to each box
  // TODO: share as much as possible of each shinyBox
  def shinyBox (size: Int) = {
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
    boxGeometry.addControl(new RigidBodyControl(0))
    boxGeometry
  }

  def node(svo: SVO): Node = {
    val node = new Node("SVO")
    subSVONode(svo) foreach node.attachChild
    node
  }

  def subSVONode(svo: SVO): Option[Spatial] = svo.node match {
    case Full(Some(_)) => Some(shinyBox(0))

    case Full(None) => None

    case Subdivided(subNodes) =>
      val subSVOs: Array[Option[Spatial]] = subNodes map subSVONode
      val subNode = new Node()
      // for each subSVO, draw it in the correct position (if it is not empty)
      for ((optionSubSVO, ix) <- subSVOs.zipWithIndex) {
        for (subSVO <- optionSubSVO) {
          subNode.attachChild(subSVO)


          val newOrigin: Vector3f = new Octant(ix).childOrigin
          subSVO.setLocalTranslation(newOrigin)
          subSVO.scale(0.5f)

        }
      }
      Some(subNode)
  }
}
