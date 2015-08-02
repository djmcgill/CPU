import org.scalatest._

import GameLogic.voxels.SVO

class SVOSpec extends FlatSpec with Matchers {
  "Inserting into a SVO" should "not throw an exception" in {
    (SVO.initialWorld)
  }
}
