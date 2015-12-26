package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.texture.Texture
import com.jme3.util.SkyFactory

class SkyboxState extends GameState {
  private def loadTexture(name: String): Texture = app.getAssetManager.loadTexture(name)
  private lazy val back   = loadTexture("Textures/GrandCanyonSkybox/grand_canyon_back.jpg")
  private lazy val front  = loadTexture("Textures/GrandCanyonSkybox/grand_canyon_front.jpg")
  private lazy val left   = loadTexture("Textures/GrandCanyonSkybox/grand_canyon_left.jpg")
  private lazy val right  = loadTexture("Textures/GrandCanyonSkybox/grand_canyon_right.jpg")
  private lazy val top    = loadTexture("Textures/GrandCanyonSkybox/grand_canyon_top.jpg")
  private lazy val bottom = loadTexture("Textures/GrandCanyonSkybox/grand_canyon_bottom.jpg")
  private lazy val skySpatial =
    SkyFactory.createSky(app.getAssetManager, left, right, front, back, top, bottom)

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getRootNode.attachChild(skySpatial)
  }

  override def cleanup(): Unit =
    app.getRootNode.detachChild(skySpatial)
}
