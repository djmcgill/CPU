package logic.voxels

import com.jme3.export.{JmeExporter, JmeImporter, Savable}
import com.jme3.math.Vector3f

/**
 * An octant's node can either be completely filled with voxels of a given element
 * (which can be None i.e. empty space) or subdivided into eight suboctants.
 */
sealed abstract class SVONode extends Savable
case class Full (var contents: Option[Block]) extends SVONode {
  def this() = this(None)
  val contentsName = "contents"
  override def write(ex: JmeExporter): Unit = {
    val capsule = ex.getCapsule(this)
    capsule.write(contents.orNull, contentsName, null)
  }

  override def read(im: JmeImporter): Unit = {
    val capsule = im.getCapsule(this)
    capsule.readSavable(contentsName, null) match {
      case block: Block => contents = Option(block)
    }
  }
}
case class Subdivided (var octants: Array[SVO]) extends SVONode {
  def this() = this(Array()) // TODO: attach warning to this, you should never call it from user code.
  val octantsName = "octants"
  override def write(ex: JmeExporter): Unit = {
    val capsule = ex.getCapsule(this)
    val savableOctants: Array[Savable] = octants map {case so: Savable => so}
    capsule.write(savableOctants, octantsName, Array[Savable]())
  }

  override def read(im: JmeImporter): Unit = {
    val capsule = im.getCapsule(this)
    val savableOctants = capsule.readSavableArray(octantsName, Array())
    octants = savableOctants map {case o: SVO => o}
  }
}

object SVO {
  lazy val minimalSubdivided = {
    def empty: SVO = new SVO(new Full(None), 0)
    def full: SVO = new SVO(new Full(Some(new Dirt())), 0)
    val arr: Array[SVO] = Array(full, empty, empty, empty, empty, empty, empty, full)
    new SVO(Subdivided(arr), 1)
  }

  lazy val minimalInserted = {
    def empty: SVO = new SVO(new Full(None), 0)
    val arr: Array[SVO] = Array(empty, empty, empty, empty, empty, empty, empty, empty)
    val world: SVO = new SVO(new Subdivided(arr), 1)
    world.insertElementAt(Some(new Dirt()), new Vector3f(0.1f, 0.1f, 0.1f), 0)
    world.insertElementAt(Some(new Dirt()), new Vector3f(0.9f, 0.9f, 0.9f), 0)
    world
  }

  lazy val size2 = {
    val world = new SVO(new Full(None), 2)
    world.insertElementAt(Some(new Dirt()), new Vector3f(0.1f, 0.1f, 0.1f), 0)
    world
  }

  lazy val initialWorld = {
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
case class SVO (var node: SVONode, var height: Int) extends Savable {
  def this() = this(new Full(None), 0)

  val nodeName = "node"
  val heightName = "height"
  override def write(ex: JmeExporter): Unit = {
    val capsule = ex.getCapsule(this)
    capsule.write(node, nodeName, new Full(None))
    capsule.write(height, heightName, 0)
  }

  override def read(im: JmeImporter): Unit = {
    val capsule = im.getCapsule(this)
    capsule.readSavable(nodeName, new Full(None)) match {
      case node: SVONode => this.node = node
    }
    height = capsule.readInt(heightName, 0)
  }

  def inBounds(v: Vector3f): Boolean = {
    def inBoundsAxis(f: Float) = 0.0 <= f && f <= 1.0
    inBoundsAxis(v.x) && inBoundsAxis(v.y) && inBoundsAxis(v.z)
  }

  def printSVO(): Unit = printSubSVO(0)

  private def printSubSVO(tabs: Int): Unit = node match {
      case Full(Some(_)) => println(("\t" * tabs) ++ "Full")
      case Full(None) => println(("\t" * tabs) ++ "_")
      case Subdivided(subNodes) =>
        subNodes foreach (subNode => subNode.printSubSVO(tabs+1))
  }

  // Could add some sort of "minimum height that we care about"?
  def insertElementPath(element: Option[Block], path: List[Octant]): Unit = ???
  def deleteNodePath(path: List[Octant]): Unit = insertElementPath(None, path)

  def deleteNodeAt(position: Vector3f, targetHeight: Int) = insertNodeAt(new Full(None), position, targetHeight)
  def insertNodeAt(newNode: SVONode, position: Vector3f, targetHeight: Int): Unit = {
    if (targetHeight < 0)
      throw new IllegalArgumentException("Can't add at a negative height.")
    if (!inBounds(position))
      //throw new IndexOutOfBoundsException("The position was not contained inside the cube.")
      return

    if (targetHeight > height)
      throw new IllegalArgumentException("Tried to add higher than the height of the octree")

    val alreadyThere: Boolean = (newNode, node) match {
      case (Full(newElement), Full(oldElement)) => newElement == oldElement
      case _ => false
    }
    if (alreadyThere) return
    if (targetHeight == height) {
      // Insert here, overwriting whatever was in there.
      node = newNode
      return
    }

    // insertionHeight < height so recurse

    // If the node is full then we need to split it up first.
    node match {
      case Subdivided(_) =>
      case Full(element) =>
        val newOctants = Array.fill(8)(new SVO(Full(element), height - 1))
        node = Subdivided(newOctants)
    }

    val newOctant = Octant.whichOctant(position)
    val newPosition: Vector3f = newOctant.toChildSpace(position)

    node match {
      case Full(_) => throw new IllegalStateException("The node should have been subdivided.")
      case Subdivided(octants) =>
        octants(newOctant.ix).insertNodeAt(newNode, newPosition, targetHeight)


        // If we've completely filled all of the subnodes, then replace it with a Full
        // Without this last section, the function is tail recursive.
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





