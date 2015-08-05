package logic.voxels

import com.jme3.math.Vector4f

/**
 * An octant's node can either be completely filled with voxels of a given element
 * (which can be None i.e. empty space) or subdivided into eight suboctants.
 */
sealed abstract class SVONode
case class Full (contents: Option[Block]) extends SVONode
case class Subdivided (octants: Array[SVO]) extends SVONode

object SVO {
  lazy val initialWorld = {
    val world = new SVO(Full(None), 5)
    val cornerPositions = Array((-0.1f, -0.1f), (-0.1f, 0.1f), (0.1f, -0.1f), (0.1f, 0.1f))
    def justBelowZAxis(dx: Float, dz: Float) = new Vector4f (dx + 0.5f, 0.4f, dz + 0.5f, 1.0f)
    val lowerHalfPositions = cornerPositions map Function.tupled(justBelowZAxis)
    lowerHalfPositions foreach (pos => world.insertElementAt(Some(new Dirt()), pos, 4))
    world.insertElementAt(Some(new Dirt()), new Vector4f (0.1f, 0.6f, 0.1f, 1.0f), 2)
    world
  }

  lazy val voxel = new SVO(Full(Some(new Dirt())), 0)
}

/**
 * Each Sparse Voxel Octree thinks that it is the cube (0,0,0) to (1,1,1)
 */
// TODO: value equality
case class SVO (var node: SVONode, height: Int) {
  def this() = this(Subdivided(Array()), 0)

  def inBounds(v: Vector4f): Boolean = {
    def inBoundsAxis(f: Float) = 0.0 <= f && f <= 1.0
    inBoundsAxis(v.x) && inBoundsAxis(v.y) && inBoundsAxis(v.z)
  }

  def whichOctant(v: Vector4f): Octant = {
    new Octant(v.x > 0.5, v.y > 0.5, v.z > 0.5)
  }

  def insertNodeAt (newNode: SVONode, position: Vector4f, insertionHeight: Int): Unit = {
    if (insertionHeight < 0)
      throw new IllegalArgumentException("Can't add at a negative height.")
    if (!inBounds(position))
      throw new IndexOutOfBoundsException("The position was not contained inside the cube.")

    if (insertionHeight > height)
      throw new IllegalArgumentException("Tried to add higher than the height of the octree")

    val alreadyThere: Boolean = (newNode, node) match {
      case (Full(newElement), Full(oldElement)) => newElement == oldElement
      case _ => false
    }
    if (alreadyThere) return

    if (insertionHeight == height) {
      // Insert here, overwriting whatever was in there.
      node = newNode
      return
    }

    // insertionHeight < height so recurse
    node match {
      case Subdivided(_) =>
      case Full(element) =>
        // If the node is full then we need to split it up first.
        val newOctants = Array.fill(8)(new SVO(Full(element), height - 1))
        node = Subdivided(newOctants)
    }

    val newOctant = whichOctant(position)
    val newPosition: Vector4f = newOctant.toChildSpace.mult(position)

    node match {
      case Full(_) => throw new IllegalStateException("The node should have been subdivided.")
      case Subdivided(octants) =>
        octants(newOctant.ix).insertNodeAt(node, newPosition, insertionHeight)

        // If we've completely filled all of the subnodes, then replace it with a Full
        octants(0).node match {
          case Subdivided(_) =>
          case Full(firstElement) =>
            val allFullWithSame = octants forall (_.node match {
              case Full(otherElement) => firstElement == otherElement
              case _ => false
            })
            if (allFullWithSame) node = Full(firstElement)
        }
    }
  }

  def insertElementAt (element: Option[Block], position: Vector4f, height: Int): Unit = {
    insertNodeAt (Full(element), position, height)
  }
}





