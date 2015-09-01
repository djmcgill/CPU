# CPU - Constructor Protector Uniter

A game I'm writing to learn Scala.

Uses JMonkeyEngine3, which uses LWJGL.

Mostly I just wanted to write a Sparse Voxel Octree, then wrote a
renderer and realised that I was pretty much halfway to a Minecraft clone.

The idea is for it to be a cross between Dwarf Fortress and Minecraft -
you give commands to your robot peons but can also assume direct control if you want to build or fight yourself.
Ideally not as cumbersome as DF but with more gameplay than MC.

Setting will be post human/robot war, none of that generic fantasy rubbish thank you very much.

## Spitballing long-term ideas:
Different robots are specialised for different things, do you keep
the artisan/non-practical ones or do you keep the oil-lines pure?
What would multiplayer be like? Raids on each other's maps?
You are a paralysed robot (just a head?) that can have a large throne built around it in order to control more minions.

## TODO before alpha:
 - left-click to place a construction order and right-click to place a deletion order
 - left-click-drag to create squares, and scroll up and down to create cuboids
 - proper textures for the cubes
   - some kind of sand or wasteland for all the existing ones
   - some kind of metal that gets constructed
 - animation for the peon models
 - multiple peons and a pooled job queue
 - sort out lighting properly, with ambient occlusion or whatever
 - pausing, with a menu to save, load, and rebind keys
   - make sure that it pauses the various timers and movement too
 - proper pathfinding using a built-in library
 - skybox
 - sort out the refactor the code for blocks and their textures
 - make the camera controls and default position somewhat sensible
 - could save images of what octants look like from a distance
   - one image for each compass direction?
   - there'd be no parallelax in a single octant of the minimum size
   - It'd look weird if it happened to octants that were too close
   - how often to update? More often for closer?
 - fluid simulation
   - see http://www.dwarfcorp.com/site/2013/06/19/how-water-works-in-dwarfcorp/
   - if too far away, could abstract to flow into and out of each octant?
     - would need to go back and forth seamlessly
   - could make sand a very limited fluid too?
 - vechicles/tools
 