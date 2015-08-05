package logic.voxels

import com.github.jpbetz.subspace._

import scala.collection.mutable

/**
 * Given an SVO and a ray, where does that ray intersect with a voxel (if it does).
 */
object RayTracer {
  def rayHit (origin: Vector4, direction: Vector4, svo : SVO) : Option[HitResult] = ???
}
