package controller

import com.jme3.app.Application
import com.jme3.app.state.AppStateManager
import com.jme3.texture.Texture
import com.jme3.util.SkyFactory

class SkyboxState extends AbstractAppStateWithApp{
  lazy val back: Texture   = app.getAssetManager.loadTexture("Textures/GrandCanyonSkybox/grand_canyon_back.jpg")
  lazy val front: Texture  = app.getAssetManager.loadTexture("Textures/GrandCanyonSkybox/grand_canyon_front.jpg")
  lazy val left: Texture   = app.getAssetManager.loadTexture("Textures/GrandCanyonSkybox/grand_canyon_left.jpg")
  lazy val right: Texture  = app.getAssetManager.loadTexture("Textures/GrandCanyonSkybox/grand_canyon_right.jpg")
  lazy val top: Texture    = app.getAssetManager.loadTexture("Textures/GrandCanyonSkybox/grand_canyon_top.jpg")
  lazy val bottom: Texture = app.getAssetManager.loadTexture("Textures/GrandCanyonSkybox/grand_canyon_bottom.jpg")
  lazy val skySpatial      = SkyFactory.createSky(app.getAssetManager,left, right, front, back, top, bottom)

  override def initialize(stateManager: AppStateManager, superApp: Application): Unit = {
    super.initialize(stateManager, superApp)
    app.getRootNode.attachChild(skySpatial)
  }

  override def cleanup(): Unit = {
    app.getRootNode.detachChild(skySpatial)
    super.cleanup()
  }
}
