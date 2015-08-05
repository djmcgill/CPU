import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.math.Vector3f
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box
import com.jme3.math.ColorRGBA

object CPU extends SimpleApplication {

  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  @Override
  def simpleInitApp() {
    val b = new Box(1, 1, 1)               // create cube shape
    val geom = new Geometry("Box", b)      // create cube geometry from the shape
    val mat = new Material(assetManager,
      "Common/MatDefs/Misc/Unshaded.j3md") // create a simple material
    mat.setColor("Color", ColorRGBA.Blue)  // set color of material to blue
    geom.setMaterial(mat);                 // set the cube's material
    rootNode.attachChild(geom);            // make the cube appear in the scene
  }
}