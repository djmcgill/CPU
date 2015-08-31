package logic.voxels

import com.jme3.export.{JmeExporter, JmeImporter, Savable}

/** The different types of voxels. This is just a placeholder for the foreseeable future.
  * This might well contain some drawing code or at least the texture positions for each one? */
sealed abstract class Block extends Savable {
  val blockID: Int


  override def hashCode(): Int = blockID.hashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case thatBlock: Block => this.blockID == thatBlock.blockID
    case _ => false
  }

}

case class Dirt() extends Block {
  override val blockID = 0
  override def write(ex: JmeExporter): Unit = {}
  override def read(im: JmeImporter): Unit = {}
}

case class Phantom(block: Block) extends Block {
  override val blockID: Int = 1
  override def write(jmeExporter: JmeExporter): Unit = ???
  override def read(jmeImporter: JmeImporter): Unit = ???
}