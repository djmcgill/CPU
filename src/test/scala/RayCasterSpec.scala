import com.jme3.math.Vector3f
import logic.voxels._
import org.scalatest._


class RayCasterSpec extends FlatSpec with Matchers {

  val Eps = 0.001
  def almostEqual(v1: Vector3f, v2: Vector3f) = {
    val diff = v1 subtract v2
    def almostZero(f: Float) = math.abs(f) < Eps
    almostZero(diff.x) && almostZero(diff.y) && almostZero(diff.z)
  }

  "A single voxel" should "be hit as expected by a horizontal line" in {
    val origin = new Vector3f(0.1f, 0.1f, 5)
    val direction = new Vector3f(0, 0, -1)
    val predeterminedHit = new Vector3f(0.1f, 0.1f, 1)
    val predeterminedPath = List()
    val Some((calculatedHit, calculatedPath)) = RayCaster.cast(origin, direction, SVO.voxel)
    almostEqual(calculatedHit, predeterminedHit) should be (true)
    calculatedPath should be (predeterminedPath)
  }

  "A single voxel" should "be hit as expected by a diagonal line" in {
    val origin = new Vector3f(0.5f, 0, 1.5f)
    val direction = new Vector3f(0, 1, -1)
    val predeterminedHit = new Vector3f(0.5f, 0.5f, 1)
    val predeterminedPath = List()
    val Some((calculatedHit, calculatedPath)) = RayCaster.cast(origin, direction, SVO.voxel)
    almostEqual(calculatedHit, predeterminedHit) should be (true)
    calculatedPath should be (predeterminedPath)
  }

  "SVO.minimalSubdivided" should "be hit as expected by a horizontal line" in {
    val origin = new Vector3f(0.2f, 0.2f, 5)
    val direction = new Vector3f(0, 0, -1)
    val predeterminedHit = new Vector3f(0.2f, 0.2f, 0.5f)
    val predeterminedPath = List(new Octant(0))
    val Some((calculatedHit, calculatedPath)) = RayCaster.cast(origin, direction, SVO.minimalSubdivided)
    almostEqual(calculatedHit, predeterminedHit) should be (true)
    calculatedPath should be (predeterminedPath)
  }
  /*
  "SVO.minimalSubdivided" should "be hit as expected by a diagonal line" in {
    val origin = new Vector3f(???)
    val direction = new Vector3f(???)
    val predeterminedHit = new Vector3f(???)
    val predeterminedPath = ???
    val Some((calculatedHit, calculatedPath)) = RayCaster.cast(origin, direction, SVO.minimalSubdivided)
    almostEqual(calculatedHit, predeterminedHit) should be (true)
    calculatedPath should be (predeterminedPath)
  }
  */
}
