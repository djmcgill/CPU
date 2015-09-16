package logic.voxels

import com.jme3.bullet.BulletAppState
import com.jme3.math.Vector3f

import scala.collection.mutable

object AStar {
  def apply(start: Vector3f, goal: Vector3f, navGrid: SVONavGrid): List[Vector3f] = {
    // TODO: also include path smoothing when possible
    // TODO: also need to work out the actual path?
    def pairWithDistance(v: Vector3f) = (v.distanceSquared(goal), v)
    val openSet = mutable.PriorityQueue[(Float, Vector3f)](pairWithDistance(start))(Ordering.by(_._1))
    val closedSet = mutable.Set[Vector3f]()
    val cameFrom = mutable.Map[Vector3f, Vector3f]()

    def canGoDirectlyTo(from: Vector3f, to: Vector3f, bulletAppState: BulletAppState): Boolean = ???


    ???
  }
}
