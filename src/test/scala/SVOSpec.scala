import org.scalatest._

import logic.voxels.SVO

class SVOSpec extends FlatSpec with Matchers {
  "Printing a subdivided SVO" should "not throw an exception" in {
    SVO.minimalSubdivided.printSVO()
  }

  "Inserting into a subdivided and then printing" should "not throw an exception" in {
    SVO.minimalInserted.printSVO()
  }
}
