package scala.GameLogic



/**
 * Created by David McGillicuddy on 30/07/2015.
 * This represents a floating viewpoint in the world.
 */
class ViewCamera (
    origin: Vec,
    rho: Float,
    theta: Float,
    fov: Float,
    world: GameState) {
  // TODO: copy from f# version
  // constants
  val RotateSpeed   = 1.0
  val PitchSpeed    = 2.0
  val PanSpeed      = 3.0
  val ScrollSpeed   = 1.0

  def scroll (distance: Float): Unit = {
    return 
  } 
}
