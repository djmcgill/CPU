package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.input.{MouseInput, KeyInput}
import com.jme3.input.controls.{MouseButtonTrigger, KeyTrigger, Trigger}

class KeyBindings extends AbstractAppStateWithApp {
  private val bindings: Map[String, Trigger] = Map(
    ("SELECT CUBOID"     , new MouseButtonTrigger(MouseInput.BUTTON_LEFT)),
    ("PLACE DIRT"        , new KeyTrigger(KeyInput.KEY_Q)),
    ("DELETE BLOCK"      , new KeyTrigger(KeyInput.KEY_W)),
    ("PLACE PHANTOM DIRT", new KeyTrigger(KeyInput.KEY_E)),

    // OverviewCameraState
    ("CAMERA TARGET LEFT"    , new KeyTrigger(KeyInput.KEY_LEFT)),
    ("CAMERA TARGET RIGHT"   , new KeyTrigger(KeyInput.KEY_RIGHT)),
    ("CAMERA TARGET FORWARD" , new KeyTrigger(KeyInput.KEY_UP   )),
    ("CAMERA TARGET BACKWARD", new KeyTrigger(KeyInput.KEY_DOWN )),
    ("CAMERA TARGET UP"      , new KeyTrigger(KeyInput.KEY_PGUP )),
    ("CAMERA TARGET DOWN"    , new KeyTrigger(KeyInput.KEY_PGDN )))

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    bindings foreach {case (name, trigger) => app.getInputManager.addMapping(name, trigger)}
  }

  override def cleanup(): Unit = {
    bindings foreach {case (name, _) => app.getInputManager.deleteMapping(name)}
    super.cleanup()
  }
}
