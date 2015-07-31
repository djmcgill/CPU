package GameLogic.voxels

import com.github.jpbetz.subspace._

/**
 * Created by David McGillicuddy on 30/07/2015.
 * An octant's node can either be completely filled with voxels of a given element
 * (which can be None i.e. empty space) or subdivided into eight suboctants.
 */
sealed abstract class SVONode
case class Full (contents: Option[Block])  extends SVONode
case class Subdivided (octets: Array[SVO]) extends SVONode

/**
 * Each Sparse Voxel Octree thinks that it is the cube (0,0,0) to (1,1,1)
 */

class SVO (node: SVONode) {
  def this() = this(Subdivided(Array()))

  def inBounds(v: Vector3) : Boolean = {
    def inBoundsAxis(f: Float) = 0.0 <= f && f <= 1.0
    inBoundsAxis(v.x) && inBoundsAxis(v.y) && inBoundsAxis(v.z)
  }

  def insertNodeAt (node: SVONode, position: Vector3, height: Int): Unit = {
    // TODO: copy from f# version
  }

  def insertElementAt (element: Block, position: Vector3, height: Int): Unit = {
    insertNodeAt (Full(Some (element)), position, height)
  }

  // TODO: copy from f# version
  val exampleWorld = ()

}







