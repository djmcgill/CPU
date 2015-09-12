package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.input.controls.AnalogListener

/**
 * Scala wrapper class for actions to be performed in response to the specified named events.
 */
abstract class AbstractAnalogListenerState extends AbstractAppStateWithApp {
  /** The actionNames that this handler is interested in. WARNING: Still need to register in KeyBindings. */
  val analogNames: List[String]

  /** The actual action to perform */
  def analog(name: String, value: Float, tpf: Float): Unit

  def analogListener = new AnalogListener {
    override def onAnalog(name: String, value: Float, tpf: Float) =
      analog(name, value, tpf)
  }

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getInputManager.addListener(analogListener, analogNames: _*)
  }

  override def cleanup(): Unit = {
    app.getInputManager.removeListener(analogListener)
  }
}
