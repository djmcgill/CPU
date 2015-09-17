package logic.voxels

import com.jme3.bullet.BulletAppState
import com.jme3.math.Vector3f

import scala.collection.mutable

object AStar {
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
        return Some(reconstructPath(cameFrom)(current))
      }
      closedSet.add(current)

      navGrid.validNeighbours(current).filter(!closedSet.contains(_)).foreach{ neighbour =>
        val pathLengthToNeighbour = pathLengthTo.get(current).get + current.distance(neighbour)
        val isFurtherThanAlreadyKnown = pathLengthTo.get(neighbour) exists (pathLengthToNeighbour > _)
        if (!isFurtherThanAlreadyKnown) {
          cameFrom     += ((neighbour, current))
          pathLengthTo += ((current, pathLengthToNeighbour))
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
      val previous = cameFrom.get(current).get
      reconstructPathGo(current :: pathSoFar)(previous)
    }
    current => reconstructPathGo(Nil)(current).reverse
  }
}
