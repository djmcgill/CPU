package logic.voxels

import com.jme3.math.{FastMath, Vector3f}

import scala.collection.mutable

// Warning: this expects the positions to be in SVO-space (i.e. between (0,0,0) and (1,1,1)) AND to path from the centre
// of the block stood on to the centre of the desired block.
/* TODO: add a method which is how it is most commonly used - convert from worldspace to SVO space,
    default values for maxIterations and closeEnough */
object AStar {
  val DefaultMaxIterations = 10000

  def pathToInWorld(startWorld: Vector3f, goalWorld: Vector3f, svoHeight: Int, navGrid: SVONavGrid): Option[List[Vector3f]] = {
    val startSVO = ??? : Vector3f
    val goalSVO = ??? : Vector3f
    val closeEnoughSVO = ??? : Float // scale FastMath.sqrt(2) to svo space
    apply(startSVO, goalSVO, DefaultMaxIterations, closeEnoughSVO, navGrid)
  }


  def apply(
    start: Vector3f,
    goal: Vector3f,
    maxIterations: Int,
    closeEnough: Float,
    navGrid: SVONavGrid)
  : Option[List[Vector3f]] = {
    def shortest[A]  = Ordering.by[(Float, A), Float](_._1).reverse
    val openSet      = mutable.PriorityQueue[(Float, Vector3f)] ((start.distance(goal), start))(shortest)
    val closedSet    = mutable.Set[Vector3f]()
    val cameFrom     = mutable.Map[Vector3f, Vector3f]()
    val pathLengthTo = mutable.Map[Vector3f, Float]((start, 0))
    for (_ <- 0 until maxIterations) {
      if (openSet.isEmpty) {return None}
      val current = openSet.dequeue()._2
      if (current.distance(goal) <= closeEnough) {
        return Some(reconstructPath(cameFrom)(current).reverse)
      }
      closedSet.add(current)

      navGrid.validNeighbours(current).filter(!closedSet.contains(_)).foreach { neighbour =>
        val pathLengthToNeighbour = pathLengthTo.get(current).get + current.distance(neighbour)
        val isFurtherThanAlreadyKnown = pathLengthTo.get(neighbour) exists (pathLengthToNeighbour > _)
        if (!isFurtherThanAlreadyKnown) {
          cameFrom     += ((neighbour, current))
          pathLengthTo += ((neighbour, pathLengthToNeighbour))
          // WARNING: Nodes will appear in the openSet multiple times.
          openSet      += ((pathLengthToNeighbour + neighbour.distance(goal), neighbour))
        }
      }
    }
    None
  }

  def reconstructPath(cameFrom: mutable.Map[Vector3f, Vector3f]): Vector3f => List[Vector3f] = {
    // TODO: use trampolining here
    def reconstructPathGo(pathSoFar: List[Vector3f])(current: Vector3f): List[Vector3f] = {
      cameFrom.get(current) match {
        case None           => pathSoFar
        case Some(previous) => reconstructPathGo(current :: pathSoFar)(previous)
      }
    }
    current => reconstructPathGo(List())(current).reverse
  }


}
