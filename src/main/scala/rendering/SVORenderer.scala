package rendering

import com.jme3.asset.AssetManager
import com.jme3.material.Material
import com.jme3.math.{Transform, Vector3f, ColorRGBA}
import com.jme3.scene.{Spatial, Geometry, Node}
import com.jme3.scene.shape.Box
import logic.voxels._


/**
 * Renders a cube between 0,0,0 and 1,1,1
 */
class SVORenderer(assetManager: AssetManager, rootNode: Node) {
  def render(svo: SVO): Unit = {
    subSVONode(svo) foreach rootNode.attachChild
  }

  def subSVONode(svo: SVO): Option[Spatial] = svo.node match {
    case Full(Some(_)) =>
      val b = new Box(Vector3f.ZERO, Vector3f.UNIT_XYZ) // create cube shape
      val blue = new Geometry("Box", b) // create cube geometry from the shape
      val mat1 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md") // create a simple material
      mat1.setColor("Color", ColorRGBA.Blue) // set color of material to blue
      blue.setMaterial(mat1) // set the cube's material
      Some(blue)
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
