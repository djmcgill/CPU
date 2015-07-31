package GameLogic

import com.github.jpbetz.subspace._

import scala.math._

/**
 * A floating viewpoint in the world.
 */

class ViewCamera (
    private var origin: Vector3,
    private var polarAngle: Float,
    private var azimuthalAngle: Float) {

  // constants
  val RotateSpeed   = 1
  val PitchSpeed    = 2
  val PanSpeed      = 3
  val ScrollSpeed   = 1

  def eyeOffset: Vector3 =
    Vector3(
      (sin(polarAngle) * cos(azimuthalAngle)).toFloat,
      cos(polarAngle).toFloat,
      (sin(polarAngle) * sin(azimuthalAngle)).toFloat)

  def moveUp(distance: Float): Unit = {
    origin += Vector3(0, 1, 0) * distance * ScrollSpeed
  }

  def moveForward(distance: Float): Unit = {
    val forwards = Vector3(-eyeOffset.x, 0, -eyeOffset.z)
    origin += forwards * distance * PanSpeed
  }

  def moveLeft(distance: Float): Unit = {
    val forwards = Vector3(-eyeOffset.x, 0.0f, -eyeOffset.z)
    val turnLeft = Quaternion.forAxisAngle(Vector3(0, 1, 0), Pi.toFloat / 2)
    val left = forwards.rotate(turnLeft)
    origin += left * distance * PanSpeed
  }

  private def lookAt(eye: Vector3, center: Vector3, up: Vector3): Matrix4x4 = {
    val f = (center - eye).normalize
    val s = f.crossProduct(up.normalize).normalize
    val u = s.crossProduct(f)

    Matrix4x4(
       s.x,  s.y,  s.z, -s.dotProduct(eye),
       u.x,  u.y,  u.z, -u.dotProduct(eye),
      -f.x, -f.y, -f.z,  f.dotProduct(eye),
         0,    0,    0,                  1)
  }

  def viewMatrix = lookAt (eyeOffset, origin, Vector3(0, 1, 0))
}
