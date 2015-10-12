package logic.voxels

import com.jme3.export.{JmeExporter, JmeImporter, Savable}
import com.jme3.math.Vector3f

/**
 * The idea is to convert a SVO into a 3D grid of adjacencies.
 * Assumptions: a peon is 1 block high and can climb up the side of single blocks like:
 *        ____        O____
 *  ____O|    -> ____|
 *
 * Also I want to attach each SVONavGrid to a spatial SVO node, so they get
 * loaded in and out when the nodes themselves do.
 */
// TODO: also include a way to change this when the svo changes

class SVONavGrid(val svo: SVO) extends Savable {

  override def write(jmeExporter: JmeExporter): Unit = ???
  override def read(jmeImporter: JmeImporter): Unit = ???
  //def this() = ???

  val subSvoNavGrids: Option[Array[SVONavGrid]] = svo.node match {
    case Subdivided(subSVOs) => Some(subSVOs map (new SVONavGrid(_)))
    case _ => None
  }

  // Given a point, is it /allowed/ to stand there, i.e. not covered by another block.
  def canWalkThere(position: Vector3f): Boolean = {
    val blockWidth = math.pow(2, -svo.height).toFloat
    val blockAbovePosition = new Vector3f(position.x, position.y + blockWidth, position.z)
    canWalkOnThere(position) && !canWalkOnThere(blockAbovePosition)
  }


  // Given a point, is that a valid block to stand on (i.e. does it have physics?)
  private def canWalkOnThere(position: Vector3f): Boolean = {
    if (!SVO.inBounds(position)) {
      return false
    }
    svo.node match {
      case Full(Some(blockState)) => blockState.isSolid
      case Full(None) => false
      case Subdivided(_) =>
        val childOctant = Octant.whichOctant(position)
        val newPosition = childOctant.toChildSpace(position)
        subSvoNavGrids.get(childOctant.ix).canWalkOnThere(newPosition)
      case _ => false
    }
  }

  // TODO: to implement Jump Point Search https://harablog.wordpress.com/2011/09/07/jump-point-search/
  // This also needs to take a previous parent (or current direction of travel).
  private def possibleStepsFrom(position: Vector3f): List[Vector3f] = {
    val scale      = math.pow(2, -svo.height).toFloat
    val North      = Vector3f.UNIT_Z mult -1
    val East       = Vector3f.UNIT_X
    val South      = Vector3f.UNIT_Z
    val West       = Vector3f.UNIT_X mult -1

    val NorthEast  = North add East
    val SouthEast  = South add East
    val SouthWest  = South add West
    val NorthWest  = North add West

    val Up         = Vector3f.UNIT_Y
    val Down       = Vector3f.UNIT_Y mult -1
    val Horizontal = Vector3f.ZERO

    for (
      cardinal <- List(
        North, East, South, West,
        NorthEast, SouthEast, SouthWest, NorthWest);
      height <- List(Horizontal, Up, Down)
    ) yield position add ((cardinal add height) mult scale)
  }

  // The position should be inside the block that the peon is standing on.
  def validNeighbours(position: Vector3f): List[Vector3f] = {
    val answer = possibleStepsFrom(position) filter canWalkThere
    answer
  }

}
