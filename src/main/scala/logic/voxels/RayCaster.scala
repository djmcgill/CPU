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

    castGo(rayOrigin, sanitisedDirection, svo, List())
      .map({case (pos, path) => (pos, path.reverse)})
  }

  private def castGo(
      rayOrigin: Vector3f,
      rayDirection: Vector3f,
      svo: SVO,
      pathSoFar: List[Octant]): Option[(Vector3f, List[Octant])] = {

    val hitPosition: Option[Vector3f] = {
      def tMinMaxAxis(eAxis: Float, fAxis: Float) = {
        // TODO: don't do the division if fAxis is near 0
        val t1: Float = eAxis/fAxis
        val t2: Float = (eAxis+1)/fAxis
        if (t1 < t2) (t1, t2) else (t2, t1)
      }
      val (tMinX, tMaxX) = tMinMaxAxis(-rayOrigin.x, rayDirection.x)
      val (tMinY, tMaxY) = tMinMaxAxis(-rayOrigin.y, rayDirection.y)
      val (tMinZ, tMaxZ) = tMinMaxAxis(-rayOrigin.z, rayDirection.z)

      val tMin = List(tMinX, tMinY, tMinZ).max
      val tMax = List(tMaxX, tMaxY, tMaxZ).min

      // Where the hit would be (if there would be one)
      // TODO: check that this is okay
      val hitPosition = (rayDirection mult tMin) add rayOrigin

      // Was there a hit?
      // TODO: check that this is okay
      if (tMin < tMax) Some(hitPosition) else None
    }

    hitPosition flatMap (hitPosition => svo.node match {
      // The node is full so that's the final hit or miss.
      case Full(Some(_)) => Some(hitPosition, pathSoFar)
      case Full(None)    => None

      // The node has been subdivided, check each of the subtrees.
      case Subdivided(subSVOs) =>
        val firstOctant = svo.whichOctant(hitPosition)

        def flipIfPossible(xyz: (Boolean, Boolean, Boolean)) = xyz match {case (x, y, z) =>
          val flipX = (o: Octant) => if (firstOctant.x != (rayDirection.x > 0)) o.flipX else o
          val flipY = (o: Octant) => if (firstOctant.y != (rayDirection.y > 0)) o.flipY else o
          val flipZ = (o: Octant) => if (firstOctant.z != (rayDirection.z > 0)) o.flipZ else o
          val newOctant = (flipX compose flipY compose flipZ)(firstOctant)
          if (newOctant != firstOctant) Stream(newOctant) else Stream()
        }
        val onlyX    = ( true, false, false)
        val onlyY    = (false,  true, false)
        val onlyZ    = (false, false,  true)
        val exceptX  = (false,  true,  true)
        val exceptY  = ( true, false,  true)
        val exceptZ  = ( true,  true, false)
        val allThree = ( true,  true,  true)

        val potentiallyHit =
          firstOctant #:: Stream(
            onlyX, onlyY, onlyZ,
            exceptX, exceptY, exceptZ,
            allThree
          ).flatMap(flipIfPossible)

        def recursiveCall(o: Octant): Option[(Vector3f, List[Octant])] = {
          val newRayOrigin = o.toChildSpace(rayOrigin)
          castGo(newRayOrigin, rayDirection, subSVOs(o.ix), o :: pathSoFar)
        }
        (potentiallyHit flatMap recursiveCall).headOption
    })
  }
}
