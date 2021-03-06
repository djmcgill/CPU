package logic.voxels

import com.jme3.math.Vector3f

object Octant {
  def whichOctant(v: Vector3f): Octant = {
    new Octant(v.x > 0.5f, v.y > 0.5f, v.z > 0.5f)
  }

  def globalToLocal(maxHeight: Int, globalPosition: Vector3f, path: List[Octant]) = {
    val scale: Float = math.pow(2f, -maxHeight).toFloat
    val svoPosition = globalPosition mult scale
    path.foldLeft(svoPosition){case (v, o) => o.toChildSpace(v)}
  }


  def getPathToLocal(v: Vector3f, maxLength: Int): Option[List[Octant]] = {
    if (!SVO.inBounds(v)) {return None}
    var currentV = v
    Some((0 until maxLength).map {case _ =>
      val o = Octant.whichOctant(currentV)
      currentV = o.toChildSpace(currentV)
      o
    }.toList)
  }

  // Should this be targetHeight instead of maxPathLength?
  def getPathToGlobal(globalPosition: Vector3f, targetHeight: Int, svoHeight: Int): Option[List[Octant]] = {
    val scale = math.pow(2, -svoHeight).toFloat
    val svoPosition: Vector3f = globalPosition mult scale
    Octant.getPathToLocal(svoPosition, svoHeight - targetHeight)
  }

}


/**
 * An Octant's int value is used to index into the array of suboctrees.
 */
case class Octant (ix : Int) {
  private def isSet (i : Int) = (this.ix & (1 << i)) != 0

  def this (x : Boolean, y : Boolean, z : Boolean) = {
    this ((if (x) 1 else 0) | ((if (y) 1 else 0) << 1) | ((if (z) 1 else 0) << 2))
  }

  def x = isSet(0)
  def y = isSet(1)
  def z = isSet(2)
  def xyz = (this.x, this.y, this.z)

  def flipX = new Octant(!x, y, z)
  def flipY = new Octant(x, !y, z)
  def flipZ = new Octant(x, y, !z)

  def childOrigin = {
    def originAxis(b: Boolean): Float = if (b) 0.5f else 0
    new Vector3f(originAxis(this.x), originAxis(this.y), originAxis(this.z))
  }

  /**
   * To use when going from the perspective of the adult to the child (i.e. ray tracing)
   * if x > 0.5
   *   then this matrix will map the x component from [0, 0.5] to [0, 1]
   *   else map [0.5, 1] to [0, 1]
   * (same for all the other axes)
   */
  def toChildSpace(oldV: Vector3f): Vector3f = {
    new Vector3f(oldV).add(childOrigin mult -1).scaleAdd(2, Vector3f.ZERO)
  }

  /**
   * To use when going from the perspective of the child to the adult (i.e. drawing)
   * if fx
   *   then this matrix will map the x component from [0, 1] to [0, 0.5]
   *   else map [0, 1] to [0.5, 1]
   * (same for all the other axes)
   */

  def fromChildSpace(oldV: Vector3f): Vector3f = {
    new Vector3f(oldV).scaleAdd(0.5f, childOrigin)
  }
}