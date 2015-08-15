package controller

import collection.mutable
import com.jme3.math.Vector3f
import logic.voxels._

/**
 * This class will modify the SVO with a given function.
 * It can modify either the block actually clicked on (with insertion = false)
 * Or the block that's created on the side of the block that is clicked on.
 *
 * It returns the path to the node that needs to be recreated (if any).
 */

abstract class AbstractSVOInsertionControl(queue: mutable.Queue[(SVONode, Vector3f)])
    extends AbstractActionListenerState {
  val node: SVONode
  val insertion: Boolean

  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    //super.action(name: String, isPressed, tpf)
    if (!isPressed) {return}
    val svo: SVO = app.getRootNode.getUserData[SVO]("svo")

    val rayOrigin = app.getCamera.getLocation
    val click2d = app.getInputManager.getCursorPosition
    def worldCoordsAtZ(z: Float) = app.getCamera.getWorldCoordinates(click2d, z)
    val rayDirection = (worldCoordsAtZ(1) subtractLocal worldCoordsAtZ(0)).normalizeLocal
    val result = RayCaster.cast(rayOrigin, rayDirection, svo)

    result foreach {case (absoluteHitPosition, path) =>
      // What's the hit position relative to the clicked on cube?
      // Note that this should be on its face.
      val relativeHitPosition = path.foldLeft(absoluteHitPosition){case (v, o) => o.toChildSpace(v)}

      // We have a point on the face of a cube, and we want to nudge it over
      // the boundary so that the insert position corresponds to the cube touching that face.
      val edges = List(
        (1.0f, relativeHitPosition.x), (0.0f, relativeHitPosition.x),
        (1.0f, relativeHitPosition.y), (0.0f, relativeHitPosition.y),
        (1.0f, relativeHitPosition.z), (0.0f, relativeHitPosition.z))
      val diffs = edges map {case (x: Float, y: Float) => math.abs(x - y)}
      val indexOfSmallestDiff: Int = diffs.zipWithIndex.minBy(_._1)._2
      val EPS = 0.0001f
      val adjustment: Vector3f = indexOfSmallestDiff match {
        case 0 => Vector3f.UNIT_X mult EPS
        case 1 => Vector3f.UNIT_X mult -EPS
        case 2 => Vector3f.UNIT_Y mult EPS
        case 3 => Vector3f.UNIT_Y mult -EPS
        case 4 => Vector3f.UNIT_Z mult EPS
        case 5 => Vector3f.UNIT_Z mult -EPS
        case _ => throw new IllegalStateException
      }
      val onBlockOrNewBlock = if (insertion) {adjustment} else {adjustment mult -1}
      queue.enqueue((node, absoluteHitPosition add onBlockOrNewBlock))
    }
  }
}
