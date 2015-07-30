package GameLogic

/**
 * Created by David McGillicuddy on 30/07/2015.
 * An Octant's int value is used to index into the array of suboctrees.
 */
class Octant (ix : Int) {
  private def isSet (i : Int) = (this.ix & (1 << i)) != 0



  def this (x : Boolean, y : Boolean, z : Boolean) = {
    this (b2i(x) | (b2i(y) << 1) | (b2i(z) << 2))
    def b2i(b: Boolean): Int = if (b) 1 else 0
  }

  def x = isSet(0)
  def y = isSet(1)
  def z = isSet(2)
  def xyz = (this.x, this.y, this.z)
}