package logic.voxels

import com.jme3.export.{JmeExporter, JmeImporter, Savable}

/** The different types of voxels. This is just a placeholder for the foreseeable future.
  * This might well contain some drawing code or at least the texture positions for each one? */
sealed case class Block(
    blockID: Int,
    solid: Boolean = true,
    opaque: Boolean = true
    ) extends Savable {
  override def write(ex: JmeExporter): Unit = ???
  override def read(im: JmeImporter): Unit = ???
}

class Dirt extends Block(0)
class Metal extends Block(1)