package GameLogic.voxels

import com.github.jpbetz.subspace._

/**
 * Created by David McGillicuddy on 30/07/2015.
 * An octant's node can either be completely filled with voxels of a given element
 * (which can be None i.e. empty space) or subdivided into eight suboctants.
 */
sealed abstract class SVONode
case class Full (contents: Option[Block])  extends SVONode
case class Subdivided (octants: Array[SVO]) extends SVONode

/**
 * Each Sparse Voxel Octree thinks that it is the cube (0,0,0) to (1,1,1)
 */

class SVO (var node: SVONode, height: Int) {
  def this() = this(Subdivided(Array()), 0)

  def inBounds(v: Vector3): Boolean = {
    def inBoundsAxis(f: Float) = 0.0 <= f && f <= 1.0
    inBoundsAxis(v.x) && inBoundsAxis(v.y) && inBoundsAxis(v.z)
  }

  def whichOctant(v: Vector3): Octant = {
    new Octant(v.x > 0.5, v.y > 0.5, v.z > 0.5)
  }

  def depth: Int = node match {
    case Full(_) => 0
    case Subdivided(octets) => (octets map (_.depth + 1)).max
  }

  def insertNodeAt (node: SVONode, position: Vector3, insertionHeight: Int): Unit = {
    if (insertionHeight < 0)
      throw new IllegalArgumentException("Can't add at a negative height.")
    if (!this.inBounds(position))
      throw new IndexOutOfBoundsException("The position was not contained inside the cube.")

    if (insertionHeight > this.height)
      throw new IllegalArgumentException("Tried to add higher than the height of the octree")

    def alreadyThere: Boolean = (node, this.node) match {
      case (Full(newElement), Full(oldElement)) => newElement == oldElement
      case _ => false
    }
    if (!alreadyThere) {
      if (insertionHeight == this.height) {
        // Insert here, overwriting whatever was in there.
        this.node = node
      } else { // insertionHeight < this.height
        // Recurse and add into a subnode.
        def nodeIsFullOf: Option[Option[Block]] = None // TODO
        // Note that if the node is full then we need to split it up first.
        for (element <- nodeIsFullOf) {
          var newOctants = Array.fill(8)(new SVO(Full(element), this.height - 1))
          this.node = Subdivided(newOctants)
        }
        // TODO: insertIntoCorrectChild
        // TODO: if we've completely filled all of the subnodes, then replace it with a Full
      }
    }
  }

  def insertElementAt (element: Block, position: Vector3, height: Int): Unit = {
    insertNodeAt (Full(Some (element)), position, height)
  }

  // TODO: copy from f# version
  val exampleWorld = ()

}







