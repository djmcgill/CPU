package logic.voxels

import com.github.jpbetz.subspace.{Vector3, Vector4, Matrix4x4}

/**
 * An Octant's int value is used to index into the array of suboctrees.
 */
class Octant (val ix : Int) {
  private def isSet (i : Int) = (this.ix & (1 << i)) != 0

  def this (x : Boolean, y : Boolean, z : Boolean) = {
    this ((if (x) 1 else 0) | ((if (y) 1 else 0) << 1) | ((if (z) 1 else 0) << 2))
  }

  def x = isSet(0)
  def y = isSet(1)
  def z = isSet(2)
  def xyz = (this.x, this.y, this.z)

  def childOrigin = {
    def originAxis(b: Boolean): Float = if (b) 0.5f else 0
    new Vector3(originAxis(this.x), originAxis(this.y), originAxis(this.z))
  }

  /**
   * To use when going from the perspective of the adult to the child (i.e. ray tracing)
   * if x > 0.5
   *   then this matrix will map the x component from [0, 0.5] to [0, 1]
   *   else map [0.5, 1] to [0, 1]
   * (same for all the other axes)
   */
  def toChildSpace: Matrix4x4 = {
    val move = Matrix4x4.forTranslation(-this.childOrigin)
    val scale = Matrix4x4.forScale(Vector3.fill(2))
    scale * move
  }

  /**
   * To use when going from the perspective of the child to the adult (i.e. drawing)
   * if fx
   *   then this matrix will map the x component from [0, 1] to [0, 0.5]
   *   else map [0, 1] to [0.5, 1]
   * (same for all the other axes)
   */
  def fromChildSpace: Matrix4x4 = {
    val move = Matrix4x4.forTranslation(this.childOrigin)
    val scale = Matrix4x4.forScale(Vector3.fill(0.5f))
    move * scale
  }
}