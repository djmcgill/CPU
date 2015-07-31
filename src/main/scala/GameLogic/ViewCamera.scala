package GameLogic

import com.github.jpbetz.subspace._

/**
 * Created by David McGillicuddy on 30/07/2015.
 * This represents a floating viewpoint in the world.
 */

class ViewCamera (
    private var origin: Vector3,
    private var rho: Float,
    private var theta: Float,
    private var fov: Float) {

  // constants
  val RotateSpeed   = 1.0f
  val PitchSpeed    = 2.0f
  val PanSpeed      = 3.0f
  val ScrollSpeed   = 1.0f

  def scroll (distance: Float): Unit = {
    origin += Vector3 (0.0f, 1.0f, 0.0f) * distance * ScrollSpeed
  } 
}
