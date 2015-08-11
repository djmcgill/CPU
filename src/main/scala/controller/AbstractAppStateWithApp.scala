package controller

import com.jme3.app.{SimpleApplication, Application}
import com.jme3.app.state.{AppStateManager, AbstractAppState}

/**
 * I got sick of writing the same boilerplace a bunch so here it is
 */
abstract class AbstractAppStateWithApp extends AbstractAppState {
  var app: SimpleApplication = _

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    superApp match {
      case simple: SimpleApplication => app = simple
      case _ => throw new ClassCastException
    }
  }
}
