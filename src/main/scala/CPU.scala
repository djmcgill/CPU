import com.jme3.app.SimpleApplication
import controller.{SVOInsertionControl, OverviewCameraControls}
import logic.voxels.SVO
import rendering.SVORenderer

object CPU extends SimpleApplication {
  var svo = SVO.minimalInserted
  var svoRenderer: SVORenderer = _
  def main(args: Array[String]): Unit = {
    CPU.start()
  }

  override def simpleInitApp() {
    stateManager.attach(new OverviewCameraControls)
    stateManager.attach(new SVOInsertionControl(svo))
    svoRenderer = new SVORenderer(assetManager)

    val svoNode = svoRenderer.node(svo)
    rootNode.attachChild(svoNode)
  }

  override def simpleUpdate(tpf: Float): Unit = {
    super.simpleUpdate(tpf)

    rootNode.detachChildNamed("SVO")
    val svoNode = svoRenderer.node(svo)
    rootNode.attachChild(svoNode)
    // TODO: now refresh only the changes to the svo
  }
}