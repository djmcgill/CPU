package controller.svoState

import com.jme3.app.Application
import com.jme3.app.state.{AppState, AbstractAppState, AppStateManager}
import com.jme3.math._
import com.jme3.scene._
import controller.AbstractAppStateWithApp
import graphics.BlockGeometries
import logic.voxels._

import scala.collection.mutable

import scala.collection.JavaConversions._


// TODO: hard code inserted block's size to 0

/**
 * Renders a svo and attaches the physics.
 * Updates the SVO but DOES NOT touch the jobs or anything.
 */
class SVOSpatialState extends AbstractAppStateWithApp {
  private val SvoRootName = "svoSpatial"
  private lazy val maxHeight = app.getRootNode.getUserData[Int]("maxHeight")
  private lazy val svo = SVO.initialWorld(maxHeight)
  private lazy val svoPhysicsState = new SVOPhysicsState
  private lazy val blockGeometries = new BlockGeometries(app.getAssetManager)
  private lazy val states: Seq[AppState] = Seq(
    // These states can all assume that the SVO and it's spatial exists and is static throughout their lifetime.
    new SVOCuboidSelectionState,
    svoPhysicsState,
    new SVOSelectVoxel
  )

  /** You can't make changes directly to the SVO or its geometry, you have to register your intention here. */
  private val insertionQueue = new mutable.Queue[(SVONode, Vector3f)]()

  def requestSVOInsertion(node: SVONode, position: Vector3f) = {
    insertionQueue.enqueue((node, position))
  }

  override def setEnabled(enabled: Boolean): Unit = {
    super.setEnabled(enabled)
    states foreach (_.setEnabled(enabled))
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)

    val maxHeight = app.getRootNode.getUserData[Int]("maxHeight")
    app.getRootNode.setUserData("svo", svo)

    stateManager.attachAll(states)

    // Create a spatial for the SVO and call it "svoSpatial".
    createSpatialFromSVO(svo) foreach {svoSpatial =>
      svoSpatial.setName(SvoRootName)
      app.getRootNode.attachChild(svoSpatial)
      val scale = math.pow(2, maxHeight).toFloat
      svoSpatial.scale(scale)
    }
  }

  // TODO: can batch the replacements of geometries by modifying the SVO first and then combining redundant paths
  override def update(tpf: Float): Unit = {
    super.update(tpf)

    // Will need to filter the queue for valid requests if we implement multiplayer.
    insertionQueue foreach { case (svoNode, position) =>
        svo.insertNodeAt (svoNode, position, 0) foreach replaceGeometryPath
    }
    insertionQueue.clear()
  }

  override def cleanup(): Unit = {
    states foreach {state => app.getStateManager.detach(state)}
    super.cleanup()
  }

  private def replaceGeometryPath(path: List[Octant]): Unit = {
    val svoSpatial = app.getRootNode.getChild(SvoRootName)
    if (svoSpatial == null) {throw new IllegalStateException("You deleted the world!!")}

    val svoToInsert: SVO = svo.getSVOPath(path)
    assert(svoToInsert.height + path.length == svo.height)

    val oldChild: Spatial = getSpatialAtAbsolute(svoSpatial, path)
    assert(oldChild.getUserData[Int]("height") == svoToInsert.height)

    svoPhysicsState.detachSVOPhysics(oldChild)

    val parentSpatial = oldChild.getParent
    parentSpatial.detachChild(oldChild)

    // Add the new spatial if it was generated.
    createSpatialFromSVO(svoToInsert) foreach { newChild =>
      newChild.setName(oldChild.getName)
      newChild.setLocalTranslation(oldChild.getLocalTranslation)

      if(svoPhysicsState.svoSpatialCollidesWithEntity(newChild, parentSpatial.getWorldTranslation)) {
        println("collision!")
        return
      } else {
        println("no collision")
      }

      parentSpatial.attachChild(newChild)
      svoPhysicsState.attachSVOPhysics(newChild)
    }
  }

  /** Turn a SVONode into a Spatial. */
  private def createSpatialFromSVO(svo: SVO): Option[Spatial] = svo.node match {
    case Full(None) => None

    case Full(Some(block)) =>
      Some(blockGeometries.getGeometryForBlock(block, svo.height))

    // Create a new node which contains the spatials of the sub-SVOs
    case Subdivided(subSVOs) =>
      val subSpatials = subSVOs map {subSVO => createSpatialFromSVO(subSVO)}
      val newNode = new Node()
      newNode.setUserData("height", svo.height)
      newNode.scale(0.5f)
      for (ix <- 0 until 8;
           subSpatial <- subSpatials(ix)) {
        subSpatial.setLocalTranslation(Octant(ix).childOrigin mult 2)
        subSpatial.setName(ix.toString)
        newNode.attachChild(subSpatial)}
      Some(newNode)
  }

  /** Go down a path until the spatial is returned. Will split nodes and overwrite geometries if necessary. */
  private def getSpatialAtAbsolute(svoSpatial: Spatial, path: List[Octant]): Spatial = {
    (svoSpatial, path) match {
      case (spatial, Nil) => spatial
      case (geo: Geometry, o :: os) =>
        val parent = geo.getParent
        val newChildNode = new Node(o.ix.toString)
        newChildNode.scale(0.5f)
        val parentHeight = parent.getUserData[Int]("height")
        newChildNode.setUserData("height", parentHeight - 1)
        newChildNode.setLocalTranslation(geo.getLocalTranslation)
        parent.detachChild(geo)
        parent.attachChild(newChildNode)
        getSpatialAtAbsolute(newChildNode, os)
        newChildNode

      case (node: Node, o :: os) => getImmediateChild(node, o.ix.toString) match {
        case Some(childNode) =>
          getSpatialAtAbsolute(childNode, os)
        case None =>
          val newChildNode = new Node(o.ix.toString)
          newChildNode.scale(0.5f)
          val parentHeight = node.getUserData[Int]("height")
          newChildNode.setUserData("height", parentHeight - 1)
          newChildNode.setLocalTranslation(o.childOrigin mult 2)
          node.attachChild(newChildNode)
          getSpatialAtAbsolute(newChildNode, os)
      }
    }
  }

  private def getImmediateChild(node: Node, childName: String): Option[Spatial] = {
    node.getChildren.toList find (_.getName == childName)
  }
}
