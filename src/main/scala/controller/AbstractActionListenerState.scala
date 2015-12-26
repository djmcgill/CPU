package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.input.controls.{ActionListener, Trigger}

/**
 * Scala wrapper class for actions to be performed in response to the specified named events.
 */
abstract class AbstractActionListenerState extends GameState {
  /** The actionNames that this handler is interested in. WARNING: Still need to register in KeyBindings. */
  val actionNames: List[String]

  /** The actual action to perform */
  def action(name: String, isPressed: Boolean, tpf: Float): Unit

  def actionListener = new ActionListener {
    override def onAction(name: String, isPressed: Boolean, tpf: Float) =
      action(name, isPressed, tpf)
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getInputManager.addListener(actionListener, actionNames: _*)
  }

  override def cleanup(): Unit = {
    super.cleanup()
    app.getInputManager.removeListener(actionListener)
  }
}
