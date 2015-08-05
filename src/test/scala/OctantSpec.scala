import com.jme3.math.Vector4f
import org.scalatest._
import logic.voxels.Octant


class OctantSpec extends FlatSpec with Matchers {
  "The boolean triple" should "correspond to the set bits" in {
    new Octant(0).xyz should be ((false, false, false))
    new Octant(1).xyz should be ((true , false, false))
    new Octant(2).xyz should be ((false, true , false))
    new Octant(3).xyz should be ((true , true , false))
    new Octant(4).xyz should be ((false, false, true ))
    new Octant(5).xyz should be ((true , false, true ))
    new Octant(6).xyz should be ((false, true , true ))
    new Octant(7).xyz should be ((true , true , true ))

    new Octant(false, false, false).ix should be (0)
    new Octant(true , false, false).ix should be (1)
    new Octant(false, true , false).ix should be (2)
    new Octant(true , true , false).ix should be (3)
    new Octant(false, false, true ).ix should be (4)
    new Octant(true , false, true ).ix should be (5)
    new Octant(false, true , true ).ix should be (6)
    new Octant(true , true , true ).ix should be (7)
  }

  val lowerHalf = new Octant(false, false, false)
  val upperHalf = new Octant(true , false, false)
  def xAxis(x: Float) = new Vector4f (x, 0, 0, 1)
  "toChildSpace" should "work correctly on the X axis" in {
    lowerHalf.toChildSpace mult xAxis(0)     should be (xAxis(0))
    lowerHalf.toChildSpace mult xAxis(0.25f) should be (xAxis(0.5f))
    lowerHalf.toChildSpace mult xAxis(0.5f)  should be (xAxis(1))

    upperHalf.toChildSpace mult xAxis(0.5f)  should be (xAxis(0))
    upperHalf.toChildSpace mult xAxis(0.75f) should be (xAxis(0.5f))
    upperHalf.toChildSpace mult xAxis(1)     should be (xAxis(1))
  }

  "fromChildSpace" should "work correctly on the X axis" in {
    lowerHalf.fromChildSpace mult xAxis(0)    should be (xAxis(0))
    lowerHalf.fromChildSpace mult xAxis(0.5f) should be (xAxis(0.25f))
    lowerHalf.fromChildSpace mult xAxis(1)    should be (xAxis(0.5f))

    upperHalf.fromChildSpace mult xAxis(0)    should be (xAxis(0.5f))
    upperHalf.fromChildSpace mult xAxis(0.5f) should be (xAxis(0.75f))
    upperHalf.fromChildSpace mult xAxis(1)    should be (xAxis(1))
  }
}