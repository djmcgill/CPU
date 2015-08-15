package logic.voxels

import com.jme3.export.{JmeExporter, JmeImporter, Savable}
import com.jme3.math.Vector3f

/**
 * An octant's node can either be completely filled with voxels of a given newNode
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

  def inBounds(v: Vector3f): Boolean = {
    def inBoundsAxis(f: Float) = 0.0 <= f && f <= 1.0
    inBoundsAxis(v.x) && inBoundsAxis(v.y) && inBoundsAxis(v.z)
  }
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

  def printSVO(): Unit = printSubSVO(0)

  private def printSubSVO(tabs: Int): Unit = node match {
      case Full(Some(_)) => println(("\t" * tabs) ++ "Full")
      case Full(None) => println(("\t" * tabs) ++ "_")
      case Subdivided(subNodes) =>
        if (subNodes.length != 8) {throw new Exception("ahh there's a subdivided here without 8 nodes")}
        subNodes foreach (subNode => subNode.printSubSVO(tabs+1))
  }

  // Get the node at a given path.
  // Returns None if the path isn't valid (because the SVO is not subdivided as expected)
  def getNodePath(path: List[Octant]): Option[SVO] = path match {
    case Nil     => Some(this)
    case o :: os => this.node match {
      case Full(_) => None
      case Subdivided(subSVOs) => subSVOs(o.ix).getNodePath(os)
    }
  }

  // Insert the given node at the end of the path. Return a path to the highest node that has changed.
  def insertNodePath(newNode: SVONode, path: List[Octant]): Option[List[Octant]] = path match {
    // Insert here
    case Nil => this.node = newNode; Some(List())

    // If it's not already there, recurse. Then try to consolidate if needed.
    case o :: os =>
      val alreadyThere: Boolean = (newNode, node) match {
        case (Full(newElement), Full(oldElement)) => newElement == oldElement
        case _ => false
      }
      if (alreadyThere) {return None}

      insertNodePath(newNode, os) flatMap {insertPath =>
        // Check to see if we've make all the subNodes the same
        val maybeOnlyNode: Option[SVONode] = this.node match {
          case Full(_) => None
          case Subdivided(subSVOs) =>
            val subNodes = subSVOs map (_.node)
            val allAreFull = subNodes forall {case Full(_) => true; case _ => false}
            if (allAreFull && subNodes.distinct.length == 1) {Some(subNodes(0))} else None
        }

        // If we have, then change this node to reflect that (and report that we've done that).
        (maybeOnlyNode map {
          onlyNode => this.node = onlyNode; Some(List())
        }).getOrElse(Some(insertPath))
      }
  }

  def deleteNodePath(path: List[Octant]): Unit = insertNodePath(Full(None), path)

  def deleteNodeAt(position: Vector3f, targetHeight: Int) = {
    insertNodeAt(new Full(None), position, targetHeight)
  }

  def insertNodeAt(newNode: SVONode, position: Vector3f, targetHeight: Int) = {
    val maybePath = Octant.getPathTo(position, this.height - targetHeight)
    maybePath flatMap (insertNodePath(newNode, _))
  }

  def insertElementAt (element: Option[Block], position: Vector3f, height: Int) = {
    insertNodeAt (Full(element), position, height)
  }
}





