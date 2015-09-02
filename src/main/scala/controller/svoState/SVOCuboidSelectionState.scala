package controller.svoState

import com.jme3.input.KeyInput
import com.jme3.input.controls._
import com.jme3.math.Vector3f
import com.jme3.renderer.RenderManager
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box
import controller.AbstractActionListenerState

class SVOCuboidSelectionState extends AbstractActionListenerState {
  private var initialPosition: Option[Vector3f] = None
  private var selectedCorners: Option[(Vector3f, Vector3f)] = None
  private lazy val geometry = {
    val geo = new Geometry("Cuboid selection", new Box(1, 1, 1))
    ??? // TODO: sort out texture and colour and transparency and stuff
    geo
  }

  override val triggers: Seq[Trigger] = Seq(new KeyTrigger(KeyInput.KEY_R))
  override val name: String = "cuboid selection"
  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    if (isPressed) {
      initialPosition = Some(pointUnderMouse())
    } else {
      // TODO: do something with the selected cuboid
      initialPosition = None
    }
  }

  // Recalculate the rectangle and update the geometry if needed
  override def update(tpf: Float): Unit = {
    super.update(tpf)
    if (initialPosition.isEmpty) {selectedCorners = None; return}
    val oldCorners = selectedCorners
    updateCorners(pointUnderMouse())

    selectedCorners foreach {case (lowerLeftCorner, upperRightCorner) if oldCorners != selectedCorners =>
        val newMesh = new Box(Vector3f.ZERO, upperRightCorner subtract lowerLeftCorner)
        geometry.setMesh(newMesh)
        geometry.setLocalTranslation(lowerLeftCorner)
    }
  }

  // Render the geometry
  override def render(rm: RenderManager): Unit = selectedCorners foreach {_ =>
      rm.renderGeometry(geometry)
  }

  private def updateCorners(currentMousePosition: Vector3f): Unit = {
    initialPosition = Some(initialPosition.getOrElse(currentMousePosition))

    val Some(corner1) = initialPosition
    val corner2 = currentMousePosition
    def floorMin(a: Float, b: Float): Float = math.floor(math.min(a, b)).toFloat
    val lowerLeftCorner = new Vector3f(
      floorMin(corner1.x, corner2.x),
      floorMin(corner1.y, corner2.y),
      floorMin(corner1.z, corner2.z))

    def ceilMax(a: Float, b: Float): Float = math.ceil(math.max(a, b)).toFloat
    val upperRightCorner = new Vector3f(
      ceilMax(corner1.x, corner2.x),
      ceilMax(corner1.y, corner2.y),
      ceilMax(corner1.z, corner2.z))

    selectedCorners = Some((lowerLeftCorner, upperRightCorner))
  }

  private def pointUnderMouse(): Vector3f = {
    val rayOrigin = app.getCamera.getLocation
    val click2d = app.getInputManager.getCursorPosition
    def worldCoordsAtZ(z: Float) = app.getCamera.getWorldCoordinates(click2d, z)
    val rayDirection = (worldCoordsAtZ(1) subtractLocal worldCoordsAtZ(0)).normalizeLocal
    val cameraTarget = app.getRootNode.getChild("Overview Camera Target").getLocalTranslation

    // The point at which the ray intersects the plane specified y = cameraTarget.y
    // y = origin.y + t*dir.y
    // (y - origin.y) / dir.y = t
    assert (rayOrigin.y >= cameraTarget.y && rayDirection.y < 0)
    val Eps = 0.01f
    if (math.abs(rayDirection.y) < Eps) {rayDirection.y = -Eps}

    val t = (cameraTarget.y - rayOrigin.y) / rayDirection.y
    val hit = rayOrigin add (rayDirection mult t)
    assert (math.abs (hit.y - cameraTarget.y) < 0.1f)
    hit
  }
}
