import com.jme3.app.SimpleApplication
import com.jme3.asset.plugins.FileLocator
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes.MeshCollisionShape
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.material.Material
import com.jme3.math.{Vector2f, ColorRGBA, Vector3f}
import com.jme3.scene.{Spatial, Node, Geometry}
import com.jme3.scene.shape.Box
import com.jme3.util.TangentBinormalGenerator
import controller._
import controller.peonState.Peon
import controller.svoState.{SVOSpatialState, SVOPhysicsState}


object Main extends SimpleApplication {
  def main(args: Array[String]): Unit = {
    Main.start()
  }

  override def simpleInitApp() {
    val bulletAppState = new BulletAppState
    bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL)

    stateManager.attachAll(
      bulletAppState,
      new OverviewCameraState,
      new SVOSpatialState,
      new SVOPhysicsState,
      new Peon
      //new WASDcontrols
    )
    assetManager.registerLocator("resources", classOf[FileLocator])

    cam.setFrustumPerspective(45f, cam.getWidth / cam.getHeight, 0.01f, 1000f)
    cam.update()

    // Lighting
    val sun = new DirectionalLight()
    sun.setDirection(new Vector3f(0,-1,-1).normalizeLocal())
    sun.setColor(ColorRGBA.White mult 1.5f)
    rootNode.addLight(sun)

    val ambient: AmbientLight = new AmbientLight()
    ambient.setColor(ColorRGBA.White)
    rootNode.addLight(ambient)

    // Corners for debugging
    renderCorners

    // TODO: Entities
    println(s"accuracy: ${bulletAppState.getPhysicsSpace.setAccuracy(0.001f)}")


  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)
    val peon = Option(rootNode.getUserData[Spatial]("peon"))
    peon foreach { actualPeon =>
      println(s"\tpeon localTranslation: ${actualPeon.getLocalTranslation}")
      println(s"\tpeon worldTranslation: ${actualPeon.getWorldTranslation}")

    }
  }

  private def renderCorners = {
    // Draw a small box at each point on the cube (0,0,0),(1,1,1)
    val cubeMaterial = {
      val boxMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")
      boxMaterial.setBoolean("UseMaterialColors",true)
      boxMaterial.setColor("Diffuse",ColorRGBA.Red)  // minimum material color
      boxMaterial.setColor("Specular",ColorRGBA.Red) // for shininess
      boxMaterial.setColor("Ambient", ColorRGBA.Red)
      boxMaterial.setFloat("Shininess", 64f) // [1,128] for shininess
      boxMaterial
    }
    val cubeGeometry = {
      val boxMesh = new Box(0.01f, 0.01f, 0.01f)
      val boxGeometry = new Geometry("corner box", boxMesh)
      TangentBinormalGenerator.generate(boxMesh)
      boxGeometry.setMaterial(cubeMaterial)
      boxGeometry
    }

    val cornerNode = new Node("corners")
    for (x <- List(0, 1); y <- List(0, 1); z <- List(0, 1)) {
      val newChild = cubeGeometry.clone()
      newChild.setLocalTranslation(x, y, z)
      cornerNode.attachChild(newChild)
    }
    rootNode.attachChild(cornerNode)
  }
}
