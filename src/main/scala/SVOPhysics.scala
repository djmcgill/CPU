import com.jme3.bullet.collision.shapes._
import com.jme3.math.Vector3f
import logic.voxels._

object SVOPhysics {
  def mesh(svo: SVO): Option[CollisionShape] = {
    // convert a cube centered on the origin to one centered on 0.5,0.5,0.5
    def fromOriginToCorner(shape: CollisionShape): CollisionShape = {
      val parent = new CompoundCollisionShape
      parent.addChildShape(shape, new Vector3f(0.5f, 0.5f, 0.5f))
      parent
    }
    meshGo(svo.height)(svo) map fromOriginToCorner
  }

  def meshGo(parentHeight: Int)(svo: SVO): Option[CollisionShape] = {
    val localScale = svo.height / (parentHeight: Float)
    svo.node match {
      case Full(None) => None
      // WARNING: THIS IS CENTERED ON THE ORIGIN
      case Full(Some(_)) => Some(new BoxCollisionShape(new Vector3f(0.5f, 0.5f, 0.5f) mult localScale))
      case Subdivided(subNodes) =>
        val shape = new CompoundCollisionShape
        val subShapes: Array[Option[CollisionShape]] = subNodes map meshGo(parentHeight)
        subShapes.zipWithIndex foreach {case (subShape, index) =>
          subShape foreach { actualShape =>
            shape.addChildShape(actualShape, new Octant(index).childOrigin mult localScale)
          }}
        Some(shape)
    }
  }
}


