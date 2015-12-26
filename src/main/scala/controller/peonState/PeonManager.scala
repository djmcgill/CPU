package controller.peonState

import com.jme3.app.state.{AppStateManager, AppState}
import com.jme3.math.Vector3f
import controller.GameState

import scala.collection.JavaConversions._
import scala.collection.mutable

class PeonManager(svoWidth: Float) extends GameState {
  lazy val peons: mutable.MutableList[Peon] = mutable.MutableList(
    new Peon(0, new Vector3f(svoWidth/2, svoWidth/2 + 2, svoWidth/2)),
    new Peon(1, new Vector3f(svoWidth/2 + 2, svoWidth/2 + 2, svoWidth/2)))

  override def stateAttached(stateManager: AppStateManager): Unit = {
    super.stateAttached(stateManager)
    stateManager.attachAll(asJavaIterable(peons.asInstanceOf[Iterable[AppState]]))
  }

  override def stateDetached(stateManager: AppStateManager): Unit = {
    super.stateDetached(stateManager)
    peons foreach stateManager.detach
  }
}
