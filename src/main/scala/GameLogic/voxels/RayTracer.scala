package GameLogic.voxels

import com.github.jpbetz.subspace._

import scala.collection.mutable

/**
 * Given an SVO and a ray, where does that ray intersect with a voxel (if it does).
 */
object RayTracer {
  def rayHit (origin: Vector4, directionRaw: Vector4, svo : SVO) : Option[Vector4] = {
    // Set up all the constants.
    val Eps = 0.0001f

    def notTooSmall(x: Float) =
      if (x == 0) Eps
      else if (x < Eps) Eps * math.signum(x)
      else x

    val direction = Vector4 (
      notTooSmall(directionRaw.x),
      notTooSmall(directionRaw.y),
      notTooSmall(directionRaw.z),
      directionRaw.w)

    /*
     * The ray is given by x = origin + t * direction
     * => t = (x - origin) / direction
     * => t = x * 1/direction - origin/direction
     * => t = x * tCoeff - tBias
     * => tCoeff := 1/direction; tBias := origin/direction
     */
    def inv(f: Float) = 1 / math.abs(f)
    val tCoeff = Vector4 (
      inv(direction.x),
      inv(direction.y),
      inv(direction.z),
      direction.w)

    def rayHitGo(parent: SVO, origin: Vector4): Option[Vector4] = {
      if (svo.node == Full(None)) return None

      // Does the ray intersect with this cube?
      val tBias = tCoeff.scale(origin)

      def tSVO(f: Float) = tCoeff * f - tBias
      val atZero = tSVO(0)
      val atOne = tSVO(1)
      val collisions = Array(
        (atZero.x, atOne.x),
        (atZero.y, atOne.y),
        (atZero.z, atOne.z))
      def sortPair(x: Float, y: Float) =
        if (x < y) (x, y) else (y, x)
      val (mins, maxes) = (collisions map Function.tupled(sortPair)).unzip
      val tMin = mins.max
      val tMax = maxes.min

      // Is there a hit?
      if (tMin > tMax) return None

      val hitPosition: Vector4 = ???

      parent.node match {
        // If we're full then we can stop
        case Full(_) => Some(hitPosition)

        // If not, then there might be empty voxels to pass through
        case Subdivided(subNodes) =>
          val firstOctant = parent.whichOctant(hitPosition)
          val otherOctants: Stream[Octant] = ???
          def recursiveCall(o: Octant): Option[Vector4] = {
            val newOrigin = Vector4(o.childOrigin, 1) + origin
            rayHitGo (subNodes(o.ix), newOrigin)
          }
          ((firstOctant #:: otherOctants) flatMap recursiveCall).headOption
      }
    }
    rayHitGo(svo, origin)

  }
}
