import com.jme3.math.Vector3f
import logic.voxels._
import org.scalatest._

class SVOSpec extends FlatSpec with Matchers {
  "SVO.minimalInserted" should "correctly produce the same results as hard coding it" in {
    SVO.minimalInserted shouldBe SVO.minimalSubdivided
  }

  it should "insert correctly into SVO.minimalInserted" in {
    val svo = SVO.minimalInserted
    svo.insertElementAt(Some(new Dirt()), new Vector3f(0.9f, 0.1f, 0.1f), 0)
    val expectedSVO = octants(1, List(0, 1, 7))
    svo shouldBe expectedSVO
  }

  it should "delete correctly from a voxel" in {
    val svo = SVO.voxel
    svo.insertElementAt(None, new Vector3f(0.3f, 0.3f, 0.3f), 0)
    svo shouldBe new SVO(Full(None), 0)
  }

  it should "delete correctly from SVO.minimalInserted" in {
    val svo = SVO.minimalInserted
    svo.insertElementAt(None, new Vector3f(0.9f, 0.9f, 0.9f), 0)
    val expectedSVO = octants(1, List(0))
    svo shouldBe expectedSVO
  }

  "SVO.size2" should "be as expected" in {
    val svo = octants(2, List())
    svo.node match {
      case Subdivided(arr) => arr(0) = octants(1, List(0))
      case _ => fail()}
    svo shouldBe SVO.size2
  }

  it should "insert correctly into size2" in {
    val expectedSVO = octants(2, List())
    expectedSVO.node match {
      case Subdivided(arr) =>
        arr(0) = octants(1, List(0, 1))
        arr(1) = octants(1, List(2))
      case _ => fail()
    }
    val svo = SVO.size2
    svo.insertNodePath(Full(Some(new Dirt())), List(Octant(0), Octant(1)))
    svo.insertNodePath(Full(Some(new Dirt())), List(Octant(1), Octant(2)))

    svo shouldBe expectedSVO
  }

  it should "return the correct update path for inserting into minimalInserted" in {
    val svo = SVO.minimalInserted
    val maybeInsertPath = svo.insertNodePath(Full(Some(new Dirt())), List(Octant(1)))
    maybeInsertPath shouldBe Some(List(Octant(1)))
  }

  it should "return the correct update path for filling minimalInserted" in {
    val svo = SVO.minimalInserted
    val paths = Array.range(1, 7) map (ix => List(Octant(ix)))

    val maybeInsertPaths: Array[Option[List[Octant]]] = paths map ((os: List[Octant]) =>
      svo.insertNodePath(Full(Some(new Dirt())), os))

    val expectedPaths = paths map ((os: List[Octant]) => Some(os))
    // The final insert should combine into a node.
    expectedPaths(5) = Some(List())

    (maybeInsertPaths zip expectedPaths) foreach {case (p, e) => p shouldBe e}
  }

  it should "return the correct update path for inserting into size2" in {
    val svo = SVO.size2
    val path = List(Octant(2), Octant(5))
    val maybeInsertPath = svo.insertNodePath(Full(Some(new Dirt())), path)
    maybeInsertPath shouldBe Some(path)
  }

  it should "return the correct update path for inserting a whole node into size2" in {

  }

  it should "return the correct update path for deleting from size2" in {

  }

  it should "return the correct update path for deleting from a Full in size2" in {

  }

  it should "return no update path when nothing was inserted" in {
    val svo = SVO.minimalInserted
    val path = List(Octant(0))
    svo.insertNodePath(Full(Some(new Dirt())), path)
    val maybeInsertPath = svo.insertNodePath(Full(Some(new Dirt())), path)
    maybeInsertPath shouldBe None
  }

  private def empty(n: Int) = new SVO(Full(None), n)
  private def full(n: Int) = new SVO(Full(Some(new Dirt())), n)
  private def octants(n: Int, indices: List[Int]) = {
    val arr = Array.tabulate[SVO](8)((index: Int) =>
      if (indices.contains(index)) {full(n-1)} else {empty(n-1)})
    new SVO(Subdivided(arr), n)
  }
  private def extractArray(svo: SVO) = svo.node match {
    case Subdivided(arr) => arr
    case Full(_) => fail()}
}
