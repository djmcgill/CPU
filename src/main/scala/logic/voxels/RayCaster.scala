package logic.voxels

import com.jme3.math.Vector3f

/**
 * A custom raycaster designed to take advantage of the structure of a SVO.
 */
object RayCaster {
  def cast (worldRayOrigin: Vector3f, rayDirection: Vector3f, svo: SVO): Option[(Vector3f, List[Octant])] = {

    val scale = math.pow(2, svo.height).toFloat

    // convert rayOrigin into svo coordinates
    val rayOrigin = worldRayOrigin mult (1/scale)

    val Eps = 0.0001f
    def nonZero(x: Float) = if (x < 0) math.min(x,-Eps) else math.max(x, Eps)
    val sanitisedDirection = new Vector3f(
      nonZero(rayDirection.x),
      nonZero(rayDirection.y),
      nonZero(rayDirection.z))

    castGo(rayOrigin, sanitisedDirection.normalize(), svo, List())
      // convert rayOrigin FROM svo coordinates and make sure that the path is the right way around
      .map({case (pos, path) => (pos mult scale, path.reverse)})
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
    if (tMin > tMax) {return None}

    // Where the hit is
    lazy val hitPosition = (rayDirection mult tMin) add rayOrigin

    // Now process what the hit means.
    svo.node match {
      // We're done, found the right voxel.
      case Full(Some(_)) if svo.height <= 0 => Some(hitPosition, pathSoFar)

      // We've found the parent of a voxel, but it's too big.
      case Full(Some(_)) =>
        def virtuallySubdivide(
            currentHitPosition: Vector3f,
            virtualPathSoFar: List[Octant],
            height: Int)
          : List[Octant] = {
          if (height <= 0) virtualPathSoFar
          else {
            val o = Octant.whichOctant(currentHitPosition)
            val childHitPosition = o.toChildSpace(currentHitPosition)
            virtuallySubdivide(childHitPosition, o :: virtualPathSoFar, height - 1)
          }
        }

        val virtualPath = virtuallySubdivide(hitPosition, List(), svo.height)
        // Remember that the path is REVERSED here
        Some(hitPosition, virtualPath ++ pathSoFar)


      // There's nothing here.
      case Full(None) => None

      // The node has been subdivided, check each of the subtrees.
      // This is a bit of a brute force way, evaluating all possibilities.
      // TODO: Another way to do this would be to follow the line, and see which of x, y, or z equal 0.5 first.
      case Subdivided(subSVOs) =>
        val firstOctant = Octant.whichOctant(hitPosition)

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
            if (allThatShouldFlipCan) Some(newOctant) else None
        }
        val onlyX    = ( true, false, false)
        val onlyY    = (false,  true, false)
        val onlyZ    = (false, false,  true)
        val exceptX  = (false,  true,  true)
        val exceptY  = ( true, false,  true)
        val exceptZ  = ( true,  true, false)
        val allThree = ( true,  true,  true)

        // It's quite important for this to be lazy as otherwise we'd end up
        // retrieving all hits when we only want the first.
        val potentiallyHit: Stream[Octant] =
          firstOctant #:: Stream(
            onlyX, onlyY, onlyZ,
            exceptX, exceptY, exceptZ,
            allThree
          ).flatMap(flipIfPossible(_).toStream)

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
