import GameLogic.Octant
import org.scalatest._

class OctantSpec extends FlatSpec with MustMatchers {
  "The boolean triple" should "correspond to the set bits" in {
    Octant(0).xyz should be ((false, false, false))
    Octant(1).xyz should be ((true , false, false))
    Octant(2).xyz should be ((false, true , false))
    Octant(3).xyz should be ((true , true , false))
    Octant(4).xyz should be ((false, false, true ))
    Octant(5).xyz should be ((true , false, true ))
    Octant(6).xyz should be ((false, true , true ))
    Octant(7).xyz should be ((true , true , true ))

    Octant(false, false, false).ix should be (0)
    Octant(true , false, false).ix should be (1)
    Octant(false, true , false).ix should be (2)
    Octant(true , true , false).ix should be (3)
    Octant(false, false, true ).ix should be (4)
    Octant(true , false, true ).ix should be (5)
    Octant(false, true , true ).ix should be (6)
    Octant(true , true , true ).ix should be (7)
  }
}