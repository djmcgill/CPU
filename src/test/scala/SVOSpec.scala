import com.jme3.math.Vector3f
import logic.voxels._
import org.scalatest._

class SVOSpec extends FlatSpec with Matchers {
  it should "correctly produce the same results as hard coding it" in {
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

  }

  it should "return the correct update path for minimalInserted" in {

  }

  it should "return the correct update path for size2" in {

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
