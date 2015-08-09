package logic.voxels

import com.jme3.math.Vector3f

/**
 * An octant's node can either be completely filled with voxels of a given element
 * (which can be None i.e. empty space) or subdivided into eight suboctants.
 */
sealed abstract class SVONode
case class Full (contents: Option[Block]) extends SVONode
case class Subdivided (octants: Array[SVO]) extends SVONode

object SVO {
  val minimalSubdivided = {
    def empty: SVO = new SVO(new Full(None), 0)
    def full: SVO = new SVO(new Full(Some(new Dirt())), 0)
    val arr: Array[SVO] = Array(full, empty, empty, empty, empty, empty, empty, full)
    new SVO(Subdivided(arr), 1)
  }
  val initialWorld = {
    println("Creating initial world")
    val world = new SVO(Full(None), 5)
    val cornerPositions = Array((-0.1f, -0.1f), (-0.1f, 0.1f), (0.1f, -0.1f), (0.1f, 0.1f))
    def justBelowZAxis(dx: Float, dz: Float) = new Vector3f(dx + 0.5f, 0.4f, dz + 0.5f)
    val lowerHalfPositions = cornerPositions map Function.tupled(justBelowZAxis)
    lowerHalfPositions foreach (pos => world.insertElementAt(Some(new Dirt()), pos, 4))
    world.insertElementAt(Some(new Dirt()), new Vector3f(0.1f, 0.6f, 0.1f), 2)
    world
  }

  val voxel = new SVO(Full(Some(new Dirt())), 0)
}

/**
 * Each Sparse Voxel Octree thinks that it is the cube (0,0,0) to (1,1,1)
 */
case class SVO (var node: SVONode, height: Int) {
  def this() = this(Subdivided(Array()), 0)

  def inBounds(v: Vector3f): Boolean = {
    def inBoundsAxis(f: Float) = 0.0 <= f && f <= 1.0
    inBoundsAxis(v.x) && inBoundsAxis(v.y) && inBoundsAxis(v.z)
  }

  def whichOctant(v: Vector3f): Octant = {
    new Octant(v.x > 0.5, v.y > 0.5, v.z > 0.5)
  }

  def printSVO(): Unit = printSubSVO(0)

  private def printSubSVO(tabs: Int): Unit = node match {
    case Full(Some(_)) => println(("\t" * tabs) ++ "Full")
    case Full(None) => println(("\t" * tabs) ++ "_")
    case Subdivided(subNodes) =>
      subNodes foreach (subNode => subNode.printSubSVO(tabs+1))
  }

  val modifyNodePath = (f: (SVONode => Option[SVONode])) => (path: List[Octant]) => {
    (path, this.node) match {
      case (List(), _) =>
        val result = f(this.node)
        this.node = result.getOrElse(Full(None))
        result
      case (o :: os, Full(element)) => ??? // split, then recurse
      case (o :: os, Subdivided(subNodes)) => ??? // recurse
    }
  }

  /**
   * Go over the whole tree, combining nodes where possible.
   */
  def cleanTree() = ??? : ()


  val getNodePath = modifyNodePath(Some(_))
  val setNodePath = (node: SVONode) => (path: List[Octant]) => {modifyNodePath(Function.const(Some(node)))(path); ()}
  val deleteNodePath = (path: List[Octant]) => {modifyNodePath(Function.const(None))(path); ()}



  // FIXME: causes stack overflow with boundless recursion
  def insertNodeAt(newNode: SVONode, position: Vector3f, insertionHeight: Int): Unit = {
    //printf("inserting at (%f, %f, %f) at height %d\n", position.x, position.y, position.z, insertionHeight)
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
    val newPosition: Vector3f = newOctant.toChildSpace(position)

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

  def insertElementAt (element: Option[Block], position: Vector3f, height: Int): Unit = {
    insertNodeAt (Full(element), position, height)
  }
}





