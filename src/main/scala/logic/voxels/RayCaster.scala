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

    val hitPosition: Option[Vector3f] = ???
    hitPosition flatMap (hitPosition => {
      svo.node match {

        // The node is full so that's the final hit.
        case Full(_) => Some(hitPosition, pathSoFar)

        // The node has been subdivided, check each of the subTrees.
        case Subdivided(subSVOs) =>
          val firstOctant = svo.whichOctant(hitPosition)

          val flipOnce: Stream[Octant] = ??? // choose 1 from x,y,z. If can flip, then do so.
          val flipTwice: Stream[Octant] = ??? // choose 2 from x,y,z. If both can flip, then do so.
          val flipThrice: Stream[Octant] = ??? // if can flip all three, then do so.

          val potentiallyHit: Stream[Octant] = firstOctant #:: (flipOnce #::: flipTwice #::: flipThrice)

          def recursiveCall(o: Octant): Option[(Vector3f, List[Octant])] = {
            val newRayOrigin = o.toChildSpace(rayOrigin)
            castGo(newRayOrigin, rayDirection, subSVOs(o.ix), o :: pathSoFar)
          }
          (potentiallyHit flatMap recursiveCall).headOption
        }
      })
    }
}
