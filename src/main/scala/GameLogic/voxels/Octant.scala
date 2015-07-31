package scala.GameLogic.voxels

/**
 * Created by David McGillicuddy on 30/07/2015.
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
}