package controller.svoState

import com.jme3.scene.Spatial.CullHint
import com.jme3.scene.{Node, Geometry, Spatial}
import controller.AbstractAppStateWithApp

class SVOViewCullingState extends AbstractAppStateWithApp {
  val svoSpatial: Spatial = ???
  var currentCullYValue: Float = ???

  override def update(tpf: Float): Unit = {
    // for each node, recurse
    // for each geometry,
      // If the cameraTarget is below its bottom, set culling to always
      // If the cameraTarget is above its top, don't do anything
      // If it's inside, then:
      // if size <= 0 then cull
      // else temporarily resize the block (need to remember to switch back afterwards)
  }

  private def cullGeometry(spatial: Spatial): Unit = {
    val lowerLimit: Float = ???
    val upperLimit: Float = ???
    val yOffset: Float = ???

      if (currentCullYValue < lowerLimit + yOffset + 1) { // If less than 1 into the cube, treat it as culled.
        spatial.setCullHint(CullHint.Always)
      } else {
        if (spatial.getCullHint == CullHint.Always) {spatial.setCullHint(CullHint.Inherit)}
        // TODO: resize based on the difference between currentCullYValue and lowerLimit
      }
  }
}
