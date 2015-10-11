import com.jme3.math.Vector3f
import controller.Placed
import logic.voxels._
import org.scalatest.{Matchers, FlatSpec}

class AStarSpec extends FlatSpec with Matchers {
  private val diag: Float => Vector3f = Vector3f.UNIT_XYZ.mult

  "AStar.apply" should "do nothing if the goal is close enough to the start" in {
    AStar(Vector3f.ZERO, Vector3f.UNIT_XYZ, 1000, 5f, new SVONavGrid(SVO.size2)) shouldBe Some(Nil)
  }

  it should "be able to climb a block diagonally" in {
    val navGrid = new SVONavGrid(SVO.minimalSubdivided)
    AStar(Vector3f.UNIT_XYZ mult 0.25f,
          Vector3f.UNIT_XYZ mult 0.75f,
          1000, 0.1f, navGrid
    ) shouldBe Some(List(Vector3f.UNIT_XYZ mult 0.75f))
  }

  private def size2StepsNavGrid = {
    val svo = SVO.empty(2)
    val paths = List(List(Octant(0), Octant(7)),
                     List(Octant(0), Octant(0)),
                     List(Octant(7), Octant(7)),
                     List(Octant(7), Octant(0)))
    paths foreach {svo.insertNodePath(Full(Some(Placed(new Dirt()))), _)}
    new SVONavGrid(svo)
  }


  it should "be able to climb 1 block in a size 2 environment" in {
    AStar(diag(0.125f), diag(0.375f), 1000, 0.1f, size2StepsNavGrid
    ) shouldBe Some(List(diag(0.375f)))
  }

  it should "be able to climb 2 blocks in a size 2 environment" in {
    AStar(diag(0.125f), diag(0.625f), 1000, 0.1f, size2StepsNavGrid
    ) shouldBe Some(List(diag(0.375f), diag(0.625f)))
  }

  it should "be able to climb 3 blocks in a size 2 environment" in {
    AStar(diag(0.125f), diag(0.875f),
      1000, 0.1f, size2StepsNavGrid
    ) shouldBe Some(List(diag(0.375f), diag(0.625f), diag(0.875f)))
  }

  it should "not be able to path from inside an object" in {
    AStar(diag(math.pow(2, -4).toFloat), diag(0.5f),
      1000, 0.1f, new SVONavGrid(SVO.initialWorld(4))
    ) shouldBe 'empty
  }

  it should "take the optimal path when many are available for size 1" in {
    val from = new Vector3f(0.25f, 0.25f, 0.25f)
    val to = new Vector3f(0.25f, 0.25f, 0.75f)
    val expectedPath =
      List(0.75f) map (new Vector3f(0.25f, 0.25f, _))

    AStar(from, to, 1000, 0.1f, new SVONavGrid(SVO.initialWorld(1))) shouldBe Some(expectedPath)
  }

  it should "take the optimal path when many are available for size 2" in {
    val from = new Vector3f(0.125f, 0.375f, 0.125f)
    val to = new Vector3f(0.125f, 0.375f, 0.875f)
    val expectedPath =
      List(0.375f, 0.625f, 0.875f) map (new Vector3f(0.125f, 0.375f, _))

    AStar(from, to, 1000, 0.01f, new SVONavGrid(SVO.initialWorld(2))) shouldBe Some(expectedPath)
  }

  it should "fail when the block can't be pathed to" in {
    val from = new Vector3f(0.125f, 0.375f, 0.125f)
    val to = new Vector3f(0.125f, 0.125f, 0.875f)
    AStar(from, to, 1000, 0.01f, new SVONavGrid(SVO.initialWorld(2))) shouldBe 'empty
  }

  "AStar.pathToInWorld" should "also work in the same situations that AStar.apply does" in {
    val from = new Vector3f(0.5f, 1.5f, 0.5f)
    val to = new Vector3f(0.5f, 0.5f, 3.5f)
    val expectedPath =
      List(1.5f, 2.5f, 3.5f) map (new Vector3f(0.5f, 1.5f, _))
    val svo = SVO.initialWorld(2)
    AStar.pathToInWorld(from, to, svo.height, new SVONavGrid(svo)) shouldBe Some(expectedPath)
  }
}
