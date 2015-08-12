package controller

import com.jme3.math.{Rectangle, Vector3f}
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.{Node, Spatial}
import com.jme3.scene.control.AbstractControl
import logic.voxels.Octant

/**
 * For selecting a whole cuboids at a time.
 */
class SVOSelectCuboidControl extends AbstractControl {
  var corners: Option[(Vector3f, Vector3f)] = None
  // var spatial


  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = ???

  override def setEnabled(enabled: Boolean): Unit = {
    super.setEnabled(enabled)
    if (enabled) {
      // hide the mouse
      val cameraTarget: Vector3f = ???
      val initialY = cameraTarget.y
      val originalCorner = ??? // intersect mouse ray with initialY
      corners = Some(originalCorner, originalCorner)
    } else {
      corners = None
    }
  }

  override def controlUpdate(tpf: Float): Unit = ??? /*corners foreach {case (corner1: Vector3f, corner2: Vector3f) =>
    val lowestCorner = new Vector3f(math.min(corner1.x, corner2.x), math.min(corner1.y, corner2.y), math.min(corner1.z, corner2.z))
    val highestCorner = new Vector3f(math.max(corner1.x, corner2.x), math.max(corner1.y, corner2.y), math.max(corner1.z, corner2.z))

    val lowestCube = Octant.getPathTo(lowestCorner)
    val highestCube = Octant.getPathTo(highestCorner)

    val minCorner: Vector3f = Octant.fromChildSpace(lowestCube, Vector3f.ZERO)
    val maxCorner: Vector3f = Octant.fromChildSpace(highestCube, Vector3f.UNIT_XYZ))

    // basically gotta do something. could just draw a the cuboid(minCorner, maxCorner)
    // but still need to turn that into a list of paths in order to insert into all the voxels in the rectangle or whatever

    spatial = ???
  } */
}
