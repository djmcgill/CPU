package controller.peonState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.bullet.control.BetterCharacterControl
import com.jme3.input.KeyInput
import com.jme3.input.controls._
import com.jme3.math._
import com.jme3.scene.{Geometry, Spatial}
import controller.AbstractAppStateWithApp

class WASDcontrols extends AbstractAppStateWithApp {
  private var peonSpatial: Spatial = _
  private var peonControl: BetterCharacterControl = _
  private var movingForwards = false
  private var movingBackwards = false
  private var turningLeft = false
  private var turningRight = false

  private val keys = Array("CharForwards", "CharBackwards", "CharTurnLeft", "CharTurnRight", "CharJump")

  def actionListener: ActionListener = new ActionListener {
    override def onAction(s: String, b: Boolean, v: Float): Unit = s match {
      case "CharForwards"  => movingForwards  = b
      case "CharBackwards" => movingBackwards = b
      case "CharTurnLeft"  => turningLeft     = b
      case "CharTurnRight" => turningRight    = b
      case "CharJump"      => if (b) {peonControl.jump()}
      case _ =>
    }
  }

  override def update(tpf: Float): Unit = {
    super.update(tpf)
    var facingAngle = 0f

    val walkDirection = new Vector3f(0, 0, 0)
    if (movingForwards) {
      walkDirection.addLocal(0, 0, 1)
    }
    if (movingBackwards) {
      walkDirection.addLocal(0, 0, -1)
    }
    if (turningLeft) {
      facingAngle = (facingAngle + FastMath.HALF_PI * tpf) % FastMath.TWO_PI
    }
    if (turningRight) {
      facingAngle = (facingAngle - FastMath.HALF_PI * tpf) % FastMath.TWO_PI
    }

    walkDirection.multLocal(100).multLocal(tpf)
    val facingRotation = new Quaternion()
    facingRotation.fromAngleAxis(facingAngle, new Vector3f(0, 1, 0))

    val finalWalkDirection = facingRotation mult walkDirection
    if (finalWalkDirection != Vector3f.ZERO) {
      println(s"walking: $finalWalkDirection at angle $facingAngle")}

    peonSpatial match {
      case geom: Geometry => geom.rotate(facingRotation)
      case node =>
    }
    peonControl.setWalkDirection(finalWalkDirection)

  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getInputManager.addListener(actionListener, keys: _*)
    peonSpatial = app.getRootNode.getUserData[Spatial]("peon")
    peonControl = peonSpatial.getControl[BetterCharacterControl](classOf[BetterCharacterControl])
  }

  override def cleanup(): Unit = {
      app.getInputManager.removeListener(actionListener)
      super.cleanup()
    }



}

