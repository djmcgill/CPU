package controller

import com.jme3.app.Application
import com.jme3.app.state.{AppStateManager, AbstractAppState}

/**
 * I got sick of writing the same boilerplate to access global state a bunch so here it is.
 */
abstract class GameState extends AbstractAppState {
  private var hiddenApp: CpuApp = _
  lazy val app: CpuApp = hiddenApp

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    superApp match {
      case app: CpuApp => hiddenApp = app
      case _ => throw new ClassCastException
    }
  }
}
