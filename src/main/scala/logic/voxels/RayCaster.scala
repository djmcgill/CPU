package logic.voxels

import com.jme3.math.Vector3f

/**
 * A custom raycaster designed to take advantage of the structure of a SVO.
 */
object RayCaster {
  def cast (rayOrigin: Vector3f, rayDirection: Vector3f, svo: SVO): Option[(Vector3f, List[Octant])] = {

    val Eps = 0.0001f
    def nonZero(x: Float) = if (x < 0) math.min(x,-Eps) else math.max(x, Eps)
    val sanitisedDirection = new Vector3f(
      nonZero(rayDirection.x),
      nonZero(rayDirection.y),
      nonZero(rayDirection.z))

    castGo(rayOrigin, sanitisedDirection.normalize(), svo, List())
      .map({case (pos, path) => (pos, path.reverse)})
  }

  private def castGo(
      rayOrigin: Vector3f,
      rayDirection: Vector3f,
      svo: SVO,
      pathSoFar: List[Octant]): Option[(Vector3f, List[Octant])] = {

    def tMinMaxAxis(o: Float, d: Float) = {
      val t1: Float = -o/d
      val t2: Float = (1-o)/d
      if (t1 < t2) (t1, t2) else (t2, t1)
    }
    val (tMinX, tMaxX) = tMinMaxAxis(rayOrigin.x, rayDirection.x)
    val (tMinY, tMaxY) = tMinMaxAxis(rayOrigin.y, rayDirection.y)
    val (tMinZ, tMaxZ) = tMinMaxAxis(rayOrigin.z, rayDirection.z)

    val tMin: Float = List(tMinX, tMinY, tMinZ, 0.0f).max
    val tMax: Float = List(tMaxX, tMaxY, tMaxZ, 100000.0f).min

    // Was there a hit?
    if (tMin > tMax) return None

    // Where the hit is
    lazy val hitPosition = (rayDirection mult tMin) add rayOrigin

    // Now process what the hit means.
    svo.node match {

      // The node is full so that's the final hit or miss.
      case Full(Some(_)) => Some(hitPosition, pathSoFar)
      case Full(None)    => None

      // The node has been subdivided, check each of the subtrees.
      // This is a bit of a brute force way, evaluating all possibilities.
      // Another way to do this would be to follow the line, and see which of x, y, or z equal 0.5 first.
      case Subdivided(subSVOs) =>
        val firstOctant = svo.whichOctant(hitPosition)

        def flipIfPossible(xyz: (Boolean, Boolean, Boolean)) = xyz match {
          case (shouldFlipX, shouldFlipY, shouldFlipZ) =>
            val canFlipX = firstOctant.x != (rayDirection.x > 0)
            val canFlipY = firstOctant.y != (rayDirection.y > 0)
            val canFlipZ = firstOctant.z != (rayDirection.z > 0)

            val allThatShouldFlipCan =
              (!shouldFlipX || canFlipX) && (!shouldFlipY || canFlipY) && (!shouldFlipZ || canFlipZ)

            lazy val flipX = (o: Octant) => if (shouldFlipX) o.flipX else o
            lazy val flipY = (o: Octant) => if (shouldFlipY) o.flipY else o
            lazy val flipZ = (o: Octant) => if (shouldFlipZ) o.flipZ else o
            lazy val newOctant = (flipX compose flipY compose flipZ)(firstOctant)
            if (allThatShouldFlipCan) Stream(newOctant) else Stream()
        }
        val onlyX    = ( true, false, false)
        val onlyY    = (false,  true, false)
        val onlyZ    = (false, false,  true)
        val exceptX  = (false,  true,  true)
        val exceptY  = ( true, false,  true)
        val exceptZ  = ( true,  true, false)
        val allThree = ( true,  true,  true)

        val potentiallyHit: Stream[Octant] =
          firstOctant #:: Stream(
            onlyX, onlyY, onlyZ,
            exceptX, exceptY, exceptZ,
            allThree
          ).flatMap(flipIfPossible)

        def recursiveCall(o: Octant): Option[(Vector3f, List[Octant])] = {
          val newRayOrigin = o.toChildSpace(rayOrigin)
          val childResult = castGo(newRayOrigin, rayDirection, subSVOs(o.ix), o :: pathSoFar)
          childResult map {case (childHitPosition: Vector3f, path: List[Octant]) =>
            (o.fromChildSpace(childHitPosition), path)}
        }
        (potentiallyHit flatMap recursiveCall).headOption
    }
  }
}
