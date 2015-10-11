package controller.peonState

import com.jme3.math.{FastMath, Vector3f}
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.control.AbstractControl
import logic.voxels.{AStar, SVONavGrid}

import scala.util.Random

class PeonJobSeeker(peonId: Long, jobStateState: JobStateState, svoNavGrid: SVONavGrid) extends AbstractControl {
  var currentJob: JobState = Idle()
  var currentPath = List[Vector3f]()

  override def controlRender(renderManager: RenderManager, viewPort: ViewPort): Unit = {}

  override def controlUpdate(v: Float): Unit = {
    // Update job state
    if (currentPath.isEmpty) {
      currentJob match {
        case Idle(center, radius) =>
          if (spatial.getWorldTranslation.distanceSquared(center) <= radius * radius) {
            currentJob = jobStateState.peonRequestJob(peonId)
          } else {
            // Move towards a random point in the idle area. When there, do nothing.
            val angle = Random.nextFloat() * FastMath.TWO_PI
            val distance = Random.nextFloat() * radius
            val point = center add new Vector3f(distance * math.cos(angle).toFloat, 0, distance * math.sin(angle).toFloat)

            val currentPositionInSvoSpace = ??? : Vector3f
            val destinationInSvoSpace = ??? : Vector3f
            val maybePath = AStar(currentPositionInSvoSpace, destinationInSvoSpace, 10000, FastMath.sqrt(2), svoNavGrid)
            currentPath = maybePath.getOrElse(Nil)
          }
        case InteractWithBlock(position) =>
          val reach = 0.5f
          if (spatial.getWorldTranslation.distanceSquared(position) <= reach * reach) {
            currentPath = Nil
            println("Successfully succeeded at getting to a place! Should probably do something now...")
          } else {
            // TODO: refactor with the above
            val currentPositionInSvoSpace = ??? : Vector3f
            val destinationInSvoSpace = ??? : Vector3f
            val maybePath = AStar(currentPositionInSvoSpace, destinationInSvoSpace, 10000, FastMath.sqrt(2), svoNavGrid)
            currentPath = maybePath.getOrElse(Nil)
          }
      }
    }

    // Move as specified by the jobstate
    currentPath match {
      case Nil => // spin slightly in place?
      case nextTarget :: rest =>
        // TODO: get all this code from the old peonJobSeeker from the history
        // Face nextTarget
        // Move forwards a bit
        // If next target is above, jump.
        // If stuck, recalculate path
    }
  }
}
