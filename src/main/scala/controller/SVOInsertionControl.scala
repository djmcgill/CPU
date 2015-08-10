package controller

import javax.swing.event.MouseInputAdapter

import com.jme3.app.{SimpleApplication, Application}
import com.jme3.app.state.{AppStateManager, AbstractAppState}
import com.jme3.input.{MouseInput, KeyInput}
import com.jme3.input.controls.{ActionListener, MouseButtonTrigger, KeyTrigger}
import com.jme3.math.Vector3f
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.{Node, Spatial}
import com.jme3.scene.control._
import logic.voxels._

/**
 * This control implements new blocks appearing in the SVO when they're clicked on.
 * It needs to update both the SVO itself and also the renderer.
 */
object SVOInsertionControl extends AbstractAppState {
  val insertionKey = "INSERT ONTO FACE"
  var app: SimpleApplication = _
  val svo: SVO = ???
  val svoNode: Node = ???

  def actionListener = new ActionListener {
    override def onAction(name: String, isPressed: Boolean, tpf: Float): Unit = {
      val rayOrigin: Vector3f = ???
      val rayDirection: Vector3f = ???
      val result: Option[(Vector3f, List[Octant])] = RayCaster.cast(rayOrigin, rayDirection, svo)

      result match {
        case None =>
        case Some ((absoluteHitPosition, path)) =>
          // go down the path to the node we care about,
          // adjusting the hit position each time
          val relativeHitPosition: Vector3f = ???

          val edges = List(
            (1, relativeHitPosition.x), (0, relativeHitPosition.x),
            (1, relativeHitPosition.y), (0, relativeHitPosition.y),
            (1, relativeHitPosition.z), (0, relativeHitPosition.z))

          val diffs = edges map {case (x: Float, y: Float) => math.abs(x - y)}
          val indexOfSmallestDiff = ???

          val adjustment: Vector3f = indexOfSmallestDiff match {
            case 0 => Vector3f.UNIT_X mult 0.0001f
            case 1 => Vector3f.UNIT_X mult -0.0001f
            case 2 => Vector3f.UNIT_Y mult 0.0001f
            case 3 => Vector3f.UNIT_Y mult -0.0001f
            case 4 => Vector3f.UNIT_Z mult 0.0001f
            case 5 => Vector3f.UNIT_Z mult -0.0001f
            case _ => ??? // throw an exception? invalid state?
          }

          // May possibly need to convert adjustment from parentSpace
          // to the scale of the smallest voxel
          val elementToInsert: Block = new Dirt()
          svo.insertElementAt(Some(elementToInsert), absoluteHitPosition add adjustment, 0)

          // TODO: also update svo rendering, in whole or in part

          svo.printSVO()
      }


    }
  }






  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getInputManager.addMapping(insertionKey, new MouseButtonTrigger(MouseInput.BUTTON_LEFT))
    app.getInputManager.addListener(actionListener, insertionKey)
  }

  override def cleanup(): Unit = {
    super.cleanup()
    app.getInputManager.deleteMapping(insertionKey)
    app.getInputManager.removeListener(actionListener)
  }
}
