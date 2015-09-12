package controller.svoState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.material.Material
import com.jme3.material.RenderState.BlendMode
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.renderer.queue.RenderQueue.Bucket
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box
import controller.{BlockStateState, BlockState, AbstractActionListenerState}
import logic.voxels._

object SVOCuboidSelectionState {
  val ChooseDirtName = "CHOOSE DIRT"
  val ChooseMetalName = "CHOOSE METAL"
  val ChooseAirName = "CHOOSE AIR"
  val NoChoiceName = "CHOOSE NOTHING"
  // CHOOSE YOUR FUTURE
  // CHOOSE LIFE

  val StartSelectionName = "SELECT CUBOID"
  val ChoiceNames = List(ChooseDirtName, ChooseMetalName, ChooseAirName, NoChoiceName)
}

class SVOCuboidSelectionState extends AbstractActionListenerState {


  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getRootNode.attachChild(SelectionBoxGeometry)
  }

  override def setEnabled(enabled: Boolean): Unit = {
    super.setEnabled(enabled)
    if (enabled) {
      app.getRootNode.attachChild(SelectionBoxGeometry)
    } else {
      app.getRootNode.detachChild(SelectionBoxGeometry)
    }
  }

  // TODO: have a cube that's displayed
  private var initialPosition: Option[Vector3f]             = None
  private var selectedCorners: Option[(Vector3f, Vector3f)] = None
  private var maybeBlockToPlace: Option[Option[Block]]      = None

  private lazy val SelectionBoxGeometry = {
    val geometry = new Geometry("Cuboid selection", new Box(1, 1, 1))
    val material = new Material(app.getAssetManager, "Common/MatDefs/Misc/Unshaded.j3md")
    geometry.setMaterial(material)

    material.setColor("Color", new ColorRGBA(1, 1, 1, 0.2f))
    material.getAdditionalRenderState.setBlendMode(BlendMode.Alpha)
    geometry.setQueueBucket(Bucket.Translucent)
    geometry
  }

  override val actionNames = SVOCuboidSelectionState.StartSelectionName :: SVOCuboidSelectionState.ChoiceNames

  // TODO: is it the chase cam that messes with the mouse cursor on left-click?
  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    if (name == SVOCuboidSelectionState.StartSelectionName) {
      if (isPressed) {
        // Start recording the selected cuboid
        initialPosition = Some(pointUnderMouse())
        app.getInputManager.setCursorVisible(false)
      } else {
        // Stop editing the cuboid, we're done.
        // TODO: add a key to cancel the cuboid insertion
        initialPosition = None
        app.getInputManager.setCursorVisible(true)

        // Insert at the center of each of the selected size-0 cubes.
        selectedCorners foreach { case (lower, upper) =>
          val Array(lowerX, lowerY, lowerZ) = lower.toArray(null) map (f => math.round(math.floor(f).toFloat))
          val Array(upperX, upperY, upperZ) = upper.toArray(null) map (f => math.round(math.ceil(f).toFloat))
          val blockStateState = app.getStateManager.getState(classOf[BlockStateState])

          for ((lower, upper) <- selectedCorners;
               maybeBlockToInsert <- maybeBlockToPlace;
               x <- lowerX until upperX;
               y <- lowerY until upperY;
               z <- lowerZ until upperZ) {
            val position = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f)
            maybeBlockToInsert match {
              case Some(blockToInsert) => blockStateState.requestPlacement(blockToInsert, position)
              case None => blockStateState.requestRemoval(position)
            }
          }
        }
      }
    } else if(isPressed) {
      print("new data chosen is: ")
      maybeBlockToPlace = () match {
        case _ if name == SVOCuboidSelectionState.ChooseDirtName => println("dirt"); Some(Some(new Dirt()))
        case _ if name == SVOCuboidSelectionState.ChooseMetalName => println("metal"); Some(Some(new Metal()))
        case _ if name == SVOCuboidSelectionState.ChooseAirName => println("air"); Some(None)
        case _ if name == SVOCuboidSelectionState.NoChoiceName => println("none"); None
      }
    }
  }

  // Recalculate the rectangle and update the geometry if needed
  override def update(tpf: Float): Unit = {
    super.update(tpf)
    val oldCorners = selectedCorners
    updateCorners(pointUnderMouse())

    selectedCorners foreach {case (lowerLeftCorner, upperRightCorner) => if (oldCorners != selectedCorners) {
        val smallVector = new Vector3f(0.1f, 0.1f, 0.1f)
        val newMesh = new Box(Vector3f.ZERO subtract smallVector, upperRightCorner subtract lowerLeftCorner add smallVector)
        SelectionBoxGeometry.setMesh(newMesh)
        SelectionBoxGeometry.setLocalTranslation(lowerLeftCorner)
      }
    }
  }

  private def updateCorners(currentMousePosition: Vector3f): Unit = {
    val corner1 = initialPosition.getOrElse(currentMousePosition)
    //initialPosition = Some(corner1)

    val corner2 = currentMousePosition
    def floorMin(a: Float, b: Float): Float = math.floor(math.min(a, b)).toFloat
    val lowerLeftCorner = new Vector3f(
      floorMin(corner1.x, corner2.x),
      floorMin(corner1.y, corner2.y),
      floorMin(corner1.z, corner2.z))

    //def ceilMax(a: Float, b: Float, min: Float): Float = math.min(min+0.01f, math.ceil(math.max(a, b)).toFloat)
    def ceilMax(a: Float, b: Float, min: Float): Float = {
      val ans: Float = math.max(a, b)
      math.ceil(if (ans == min) ans + 0.01f else ans).toFloat
    }
    val upperRightCorner = new Vector3f(
      ceilMax(corner1.x, corner2.x, lowerLeftCorner.x),
      ceilMax(corner1.y, corner2.y, lowerLeftCorner.y),
      ceilMax(corner1.z, corner2.z, lowerLeftCorner.z))

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
    val Eps = 0.01f
    if (math.abs(rayDirection.y) < Eps) {rayDirection.y = -Eps}
    assert (rayDirection.y < 0) // TODO: allow for this case

    val t = (cameraTarget.y - rayOrigin.y) / rayDirection.y
    val hit = rayOrigin add (rayDirection mult t)
    assert (math.abs (hit.y - cameraTarget.y) < 0.1f)
    hit
  }
}
