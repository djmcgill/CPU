package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.input.controls.{ActionListener, Trigger}

/**
 * Scala wrapper class for a single action to be performed in response to the specified triggers.
 */
abstract class AbstractActionListenerState extends AbstractAppStateWithApp {
  /** Which triggers should this action respond to? */
  val triggers: Seq[Trigger]

  /** The name of this action. WARNING: this needs to be unique and it isn't checked. */
  val name: String

  /** The actual action to perform */
  def action(name: String, isPressed: Boolean, tpf: Float): Unit

  def actionListener = new ActionListener {
    // Do we really need to specify all the parameters?
    override def onAction(name: String, isPressed: Boolean, tpf: Float) =
      action(name, isPressed, tpf)
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getInputManager.addMapping(name, triggers: _*)
    app.getInputManager.addListener(actionListener, name)
  }

  override def cleanup(): Unit = {
    super.cleanup()
    app.getInputManager.deleteMapping(name)
    app.getInputManager.removeListener(actionListener)
  }
}
