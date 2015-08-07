import com.jme3.math.Vector3f
import logic.voxels._
import org.scalatest._

class RayCasterSpec extends FlatSpec with Matchers {
  val voxel = new SVO(Full(None), 0)
  "A single voxel" should "be hit as expected" in {
    val origin: Vector3f = new Vector3f(0.1f, 0.1f, 5)
    val direction: Vector3f = new Vector3f(0, 0, -1)
    val predeterminedHit: Vector3f = new Vector3f(0.1f, 0.1f, 1)
    RayCaster.cast(origin, direction, voxel) should be (Some(predeterminedHit, List()))

  }


}
