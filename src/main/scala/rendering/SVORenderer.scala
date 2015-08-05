package rendering

import com.jme3.asset.AssetManager
import com.jme3.material.Material
import com.jme3.math.{Vector3f, ColorRGBA}
import com.jme3.scene.{Spatial, Geometry, Node}
import com.jme3.scene.shape.Box
import logic.voxels._


/**
 * Renders a cube between 0,0,0 and 1,1,1
 */
class SVORenderer(assetManager: AssetManager, rootNode: Node) {
  def render(svo: SVO): Unit = {
    rootNode.attachChild(subSVONode(svo))
  }

  def subSVONode(svo: SVO): Spatial = svo.node match {
    case Full(_) =>
      val b = new Box(1, 1, 1)               // create cube shape
      val blue = new Geometry("Box", b)      // create cube geometry from the shape
      val mat1 = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md") // create a simple material
      mat1.setColor("Color", ColorRGBA.Blue)  // set color of material to blue
      blue.setMaterial(mat1)                 // set the cube's material
      blue

    case Subdivided(subNodes) =>
      val subSVOs: Array[Spatial] = subNodes map subSVONode
      val node = new Node()
      // for each subSVO, draw it in the correct position
      for ((subSVO, ix) <- subSVOs.zipWithIndex) {
        subSVO.scale(0.5f)
        val newOrigin: Vector3f = ??? // new Octant(ix).childOrigin
        subSVO.setLocalTranslation(newOrigin)
        node.attachChild(subSVO)
      }
      node

  }



  /*
  member this.DrawFrom (svo : SparseVoxelOctree<Option<Block>>) (m : Matrix) (vp : Matrix) =
    let mutable mvp = m * vp
  GL.UniformMatrix4(matrixID, false, &mvp)
  GL.ActiveTexture TextureUnit.Texture0
  GL.BindTexture(TextureTarget.Texture2D, textureID)
  GL.Uniform1(textureID, 0)

  let drawSubSVO octant subSVO = this.DrawFrom subSVO (fromChildSpace octant * m) vp
  match svo.Nodes with
  | Full None -> ()
  | Full _ -> cube.Draw ()
  | Subdivided arr -> Array.iteri drawSubSVO arr

  // Given a SVO, draw it between the bounds of (0,0,0) and (1,1,1)
  member this.Draw (svo : SparseVoxelOctree<Option<Block>>) (vp : Matrix) =
    this.DrawFrom svo Matrix.Identity vp
   */
}
