import com.jme3.math.Vector3f
import logic.voxels.Octant
import org.scalatest._


class OctantSpec extends FlatSpec with Matchers {
  "The boolean triple" should "correspond to the set bits" in {
    new Octant(0).xyz shouldBe (false, false, false)
    new Octant(1).xyz shouldBe (true , false, false)
    new Octant(2).xyz shouldBe (false, true , false)
    new Octant(3).xyz shouldBe (true , true , false)
    new Octant(4).xyz shouldBe (false, false, true )
    new Octant(5).xyz shouldBe (true , false, true )
    new Octant(6).xyz shouldBe (false, true , true )
    new Octant(7).xyz shouldBe (true , true , true )

    new Octant(false, false, false).ix shouldBe 0
    new Octant(true , false, false).ix shouldBe 1
    new Octant(false, true , false).ix shouldBe 2
    new Octant(true , true , false).ix shouldBe 3
    new Octant(false, false, true ).ix shouldBe 4
    new Octant(true , false, true ).ix shouldBe 5
    new Octant(false, true , true ).ix shouldBe 6
    new Octant(true , true , true ).ix shouldBe 7
  }

  private val lowerHalf = new Octant(false, false, false)
  private val upperHalf = new Octant(true , false, false)
  private def xAxis(x: Float) = new Vector3f (x, 0, 0)

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
    new Octant(false, false, false).flipX.xyz shouldBe
      (true, false, false)

    new Octant(true , false, false).flipX.xyz shouldBe
      (false, false, false)

    new Octant(false, false, false).flipX.flipZ.xyz shouldBe
      (true, false, true)

    new Octant(true, true, true).flipX.xyz shouldBe
      (false, true, true)

    new Octant(true, true, false).flipX.flipY.xyz shouldBe
      (false, false, false)

    new Octant(false, false, false).flipX.flipY.flipZ.xyz shouldBe
      (true, true, true)
  }

  "getPathToLocal" should "behave as expected" in {
    Octant.getPathToLocal(Vector3f.ZERO, 3) shouldBe
      Some(List(0, 0, 0) map (new Octant(_)))
    Octant.getPathToLocal(Vector3f.UNIT_XYZ, 4) shouldBe
      Some(List(7, 7, 7, 7) map (new Octant(_)))
    Octant.getPathToLocal(new Vector3f(0.3f, 0.3f, 0.3f), 3) shouldBe
      Some(List(0, 7, 0) map (new Octant(_)))
  }
}