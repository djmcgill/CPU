import com.jme3.math.Vector3f
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
  def xAxis(x: Float) = new Vector3f (x, 0, 0)

  "toChildSpace" should "work correctly on the X axis" in {
    lowerHalf.toChildSpace(xAxis(0))     shouldBe xAxis(0)
    lowerHalf.toChildSpace(xAxis(0.25f)) shouldBe xAxis(0.5f)
    lowerHalf.toChildSpace(xAxis(0.5f))  shouldBe xAxis(1)

    upperHalf.toChildSpace(xAxis(0.5f))  shouldBe xAxis(0)
    upperHalf.toChildSpace(xAxis(0.75f)) shouldBe xAxis(0.5f)
    upperHalf.toChildSpace(xAxis(1))     shouldBe xAxis(1)
  }

  "fromChildSpace" should "work correctly on the X axis" in {
    lowerHalf.fromChildSpace(xAxis(0))    shouldBe xAxis(0)
    lowerHalf.fromChildSpace(xAxis(0.5f)) shouldBe xAxis(0.25f)
    lowerHalf.fromChildSpace(xAxis(1))    shouldBe xAxis(0.5f)

    upperHalf.fromChildSpace(xAxis(0))    shouldBe xAxis(0.5f)
    upperHalf.fromChildSpace(xAxis(0.5f)) shouldBe xAxis(0.75f)
    upperHalf.fromChildSpace(xAxis(1))    shouldBe xAxis(1)
  }

  "flipX, flipY, and flipZ" should "correctly flip a bit" in {
    new Octant(false, false, false)
      .flipX.xyz             shouldBe ( true, false, false)

    new Octant(true , false, false)
      .flipX.xyz             shouldBe (false, false, false)

    new Octant(false, false, false)
      .flipX.flipZ.xyz       shouldBe ( true, false,  true)

    new Octant(true, true, true)
      .flipX.xyz             shouldBe (false,  true,  true)

    new Octant(true, true, false)
      .flipX.flipY.xyz       shouldBe (false, false, false)

    new Octant(false, false, false)
      .flipX.flipY.flipZ.xyz shouldBe ( true,  true,  true)
  }

  "getPathTo" should "behave as expected" in {
    Octant.getPathTo(Vector3f.ZERO, 3) shouldBe Some(List(0, 0, 0) map (ix => new Octant(ix)))
    Octant.getPathTo(Vector3f.UNIT_XYZ, 4) shouldBe Some(List(7, 7, 7, 7) map (ix => new Octant(ix)))

    // TODO: THIS IS FAILING
    Octant.getPathTo(new Vector3f(0.3f, 0.3f, 0.3f), 3) shouldBe Some(List(0, 7, 0) map (ix => new Octant(ix)))
  }
}