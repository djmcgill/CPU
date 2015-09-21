# CPU - Constructor Protector Uniter

A game I'm writing to learn Scala.

Uses JMonkeyEngine3, which uses LWJGL.

Mostly I just wanted to write a Sparse Voxel Octree, then wrote a
renderer and realised that I was pretty much halfway to a Minecraft clone.
But I can't be bothered to do all the chores myself so you'll get to tell peons to do them instead.

Setting will be post human/robot war, none of that generic fantasy rubbish thank you very much.
You are a paralysed robot (just a head?) that can have a large throne built around it in order to control more minions.

## TODO before alpha:
 - animation for the peon models
 - multiple peons and a pooled job queue
 - sort out lighting properly, with ambient occlusion or whatever
 - GUIs
   - pausing (save, load, options)
   - insert mode with different editing options
   - main menu (new, continue, load, options)
 - integrate pathfinding (including jumping)
 - sort out controls (assume mouse)
 - sort out cut-away of blocks that are above the cameraTarget

## Later TODOS:
 - fluid simulation
   - see http://www.dwarfcorp.com/site/2013/06/19/how-water-works-in-dwarfcorp/
   - if too far away, could abstract to flow into and out of each octant?
     - would need to go back and forth seamlessly
   - could make sand a very limited fluid too?
 - fighting
 - tools
 - could save images of what octants look like from a distance (or maybe distance fall-off?)
   - one image for each compass direction?
   - there'd be no parallelax in a single octant of the minimum size
   - It'd look weird if it happened to octants that were too close
   - how often to update? More often for closer?
 - there should be two types of subdivided
   - if height > BRANCHING_LIMIT then SubdividedOctrees else Subdivided3DArray
   - octrees better for large volumes
   - arrays better for very varied expanses
 - vehicles
 - give set cull hint to the SVO
 
