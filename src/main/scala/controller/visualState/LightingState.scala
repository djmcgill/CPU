package controller.visualState

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{ColorRGBA, Vector3f}
import controller.GameState

class LightingState extends GameState {
  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)

    val sun = new DirectionalLight()
    sun.setDirection(new Vector3f(0,-1,-1).normalizeLocal())
    sun.setColor(ColorRGBA.White mult 1.5f)
    app.getRootNode.addLight(sun)

    val ambient = new AmbientLight()
    ambient.setColor(ColorRGBA.White)
    app.getRootNode.addLight(ambient)
  }
}
