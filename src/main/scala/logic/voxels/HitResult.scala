package logic.voxels

import com.github.jpbetz.subspace.Vector4

case class HitResult (hitPosition: Vector4, pathToHit: List[Octant], faceHit: Int) {
  def finalise = new HitResult(hitPosition, pathToHit.reverse, faceHit)
  def appendOctant(o: Octant) = new HitResult(hitPosition, o :: pathToHit, faceHit)
}
