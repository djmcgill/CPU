import com.jme3.bullet.collision.shapes._
import com.jme3.math.Vector3f
import logic.voxels._

object SVOPhysics {
  mesh(svo: SVO): Option[CollisionShape] = {
    // convert a cube centered on the origin to one centered on 0.5,0.5,0.5
    def fromOriginToCorner(shape: CollisionShape): CollisionShape = ???
    meshGo(svo) map fromOriginToCorner
  }
  
  
  meshGo(svo: SVO): Option[CollisionShape] = {
    svo.node match {
      Full(None) => None
      // WARNING: THIS IS CENTERED ON THE ORIGIN
      Full(Some(_)) => Some(new BoxCollisionShape(0.5f, 0.5f, 0.5f))
      Subdivided(subNodes) =>
        val shape: CollisionShape = new CompoundCollisionShape
        val subShapes: List[Option[CollisionShape]] = ???
        val subTranslations: List[Vector3f] = ???
        (subShapes zip subOrigins) map shape.addChildShape(_, _)
    }
  }
}


