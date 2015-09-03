package controller.svoState.placement

import controller.AbstractActionListenerState
import logic.voxels._

class SVOPlacementState extends AbstractActionListenerState {
  var maybeChosenBlock: Option[Option[Block]] = None

  val chooseDirtName = "CHOOSE DIRT"
  val chooseMetalName = "CHOOSE METAL"
  val chooseAirName = "CHOOSE AIR"
  val noChoiceName = "CHOOSE NOTHING"


  override val names = List(chooseDirtName, chooseMetalName, chooseAirName, noChoiceName)

  /** The actual action to perform */
  override def action(name: String, isPressed: Boolean, tpf: Float): Unit = {
    if (!isPressed) {return}
    print("new block chosen is: ")
    maybeChosenBlock = () match {
      case _ if name == chooseDirtName  => println("dirt"); Some(Some(new Dirt()))
      case _ if name == chooseMetalName => println("metal"); Some(Some(new Metal()))
      case _ if name == chooseAirName   => println("air"); Some(None)
      case _ if name == noChoiceName    => println("none"); None
    }
  }
}
