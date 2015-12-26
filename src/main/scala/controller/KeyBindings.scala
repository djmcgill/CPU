package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.input.{MouseInput, KeyInput}
import com.jme3.input.controls.{MouseButtonTrigger, KeyTrigger, Trigger}
import controller.svoState.{SvoSelectVoxel, SvoCuboidSelectionState}
import controller.visualState.OverviewCameraState

class KeyBindings extends GameState {
  private val bindings: Map[String, Trigger] = Map(
    (SvoSelectVoxel.SelectVoxelName, new KeyTrigger(KeyInput.KEY_Q)),

    (OverviewCameraState.TargetLeftName    , new KeyTrigger(KeyInput.KEY_LEFT)),
    (OverviewCameraState.TargetRightName   , new KeyTrigger(KeyInput.KEY_RIGHT)),
    (OverviewCameraState.TargetForwardName , new KeyTrigger(KeyInput.KEY_UP   )),
    (OverviewCameraState.TargetBackwardName, new KeyTrigger(KeyInput.KEY_DOWN )),
    (OverviewCameraState.TargetUpName      , new KeyTrigger(KeyInput.KEY_PGUP )),
    (OverviewCameraState.TargetDownName    , new KeyTrigger(KeyInput.KEY_PGDN )),

    (OverviewCameraState.ZoomInName , new KeyTrigger(KeyInput.KEY_EQUALS)),
    (OverviewCameraState.ZoomOutName, new KeyTrigger(KeyInput.KEY_MINUS)),

    (SvoCuboidSelectionState.StartSelectionName, new MouseButtonTrigger(MouseInput.BUTTON_LEFT)),
    (SvoCuboidSelectionState.ChooseDirtName    , new KeyTrigger(KeyInput.KEY_4)),
    (SvoCuboidSelectionState.ChooseMetalName   , new KeyTrigger(KeyInput.KEY_3)),
    (SvoCuboidSelectionState.ChooseAirName     , new KeyTrigger(KeyInput.KEY_2)),
    (SvoCuboidSelectionState.NoChoiceName      , new KeyTrigger(KeyInput.KEY_1))
  )

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    bindings foreach {case (name, trigger) => app.getInputManager.addMapping(name, trigger)}
  }

  override def cleanup(): Unit = {
    bindings.keys foreach app.getInputManager.deleteMapping
    super.cleanup()
  }
}
