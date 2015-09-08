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
 - right-click (and drag) to place a deletion order
 - animation for the peon models
 + multiple peons and a pooled job queue
 - sort out lighting properly, with ambient occlusion or whatever
 - GUIs
   - pausing (save, load, options)
   - insert mode with different editing options
   - main menu (new, continue, load, options)
   - options (graphics, controls)
 - proper pathfinding using a built-in library
 - fighting
 - tools
 - fluid simulation
   - see http://www.dwarfcorp.com/site/2013/06/19/how-water-works-in-dwarfcorp/
   - if too far away, could abstract to flow into and out of each octant?
     - would need to go back and forth seamlessly
   - could make sand a very limited fluid too?
 - refactor data insertion:
    1) make data phantom
    2) enqueue job
    3) wait for worker to get there (implement with callback)
        4) worker deletes old data (if there was one)
        5) check for a collision where we would place it 
        6) worker places new data (if required)
 - overhaul job system
   - when a worker is idle, polls the job queue every so often (and picks one nearby?)
   - accepting a job (with timeout) removes it from the global queue
 - when selecting a cuboid: if cheatmode then just replace, else normal data insertion
   
## Later TODOS:
 - could save images of what octants look like from a distance
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
 
