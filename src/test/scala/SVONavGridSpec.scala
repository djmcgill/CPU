import com.jme3.math.Vector3f
import controller.Placed
import logic.voxels._
import org.scalatest.{Matchers, FlatSpec}



class SVONavGridSpec extends FlatSpec with Matchers {
  private val diag: Float => Vector3f = Vector3f.UNIT_XYZ.mult

  it should "correctly calculate the neighbour in minimalInserted" in {
    val navSpec = new SvoNavGrid(SVO.minimalInserted)
    navSpec.validNeighbours(diag(0.25f)) shouldBe List(diag(0.75f))
  }

  private def size2StepsNavGrid = {
    val svo = SVO.empty(2)
    val paths = List(List(Octant(0), Octant(7)),
                     List(Octant(0), Octant(0)),
                     List(Octant(7), Octant(7)),
                     List(Octant(7), Octant(0)))
    paths foreach {svo.insertNodePath(Full(Some(Placed(new Dirt()))), _)}
    new SvoNavGrid(svo)
  }

  it should "correctly should calculate the neighbour for a size 2 world" in {
    size2StepsNavGrid.validNeighbours(diag(0.125f)) shouldBe List(diag(0.375f))
  }

  it should "correctly should calculate the neighbour for another point in a size 2 world" in {
    size2StepsNavGrid.validNeighbours(diag(0.625f)).toSet shouldBe List(diag(0.375f), diag(0.875f)).toSet
  }

  // TODO: test canStandOn
}
