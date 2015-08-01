import org.scalatest._
import GameLogic.voxels.Octant
import com.github.jpbetz.subspace._


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
}