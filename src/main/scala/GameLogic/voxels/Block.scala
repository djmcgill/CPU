package scala.GameLogic.voxels

/**
 * Created by David McGillicuddy on 30/07/2015.
 *
 * The different types of voxels. This is just a placeholder for the foreseeable future.
 * This might well contain some drawing code or at least the texture positions for each one?
 */
sealed abstract class Block
case class Dirt() extends Block

