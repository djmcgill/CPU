package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{ColorRGBA, Vector3f}

class LightingState extends AbstractAppStateWithApp {
  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    val rootNode = app.getRootNode

    val sun = new DirectionalLight()
    sun.setDirection(new Vector3f(0,-1,-1).normalizeLocal())
    sun.setColor(ColorRGBA.White mult 1.5f)
    rootNode.addLight(sun)

    val ambient: AmbientLight = new AmbientLight()
    ambient.setColor(ColorRGBA.White)
    rootNode.addLight(ambient)
  }
}
