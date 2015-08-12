package logic.voxels

import com.jme3.export.{JmeExporter, JmeImporter, Savable}

/**
 * Created by David McGillicuddy on 30/07/2015.
 *
 * The different types of voxels. This is just a placeholder for the foreseeable future.
 * This might well contain some drawing code or at least the texture positions for each one?
 */
sealed abstract class Block extends Savable
case class Dirt() extends Block {
  override def write(ex: JmeExporter): Unit = {}
  override def read(im: JmeImporter): Unit = {}
}

