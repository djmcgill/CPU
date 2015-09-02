package graphics

import com.jme3.asset.AssetManager
import com.jme3.material.Material
import com.jme3.material.RenderState.BlendMode
import com.jme3.math.{ColorRGBA, Vector2f, Vector3f}
import com.jme3.renderer.queue.RenderQueue.Bucket
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box
import com.jme3.texture.Texture
import com.jme3.util.TangentBinormalGenerator
import logic.voxels._

/** The geometries for the various blocks. */
class BlockGeometries(assetManager: AssetManager) {

  def getGeometryForBlock(block: Block, height: Int): Geometry = block match {
    case Dirt() => dirtBox(height)
    case Metal() => metalBox(height)
    case Phantom(realBlock) => phantom(getGeometryForBlock(realBlock, height))
  }

  /** Make a block transparent */
  private def phantom(block: Geometry): Geometry = {
    val newBlock = block.clone
    val newMaterial = newBlock.getMaterial.clone
    newMaterial.setColor("Diffuse", new ColorRGBA(1, 1, 1, 0.7f))
    newMaterial.getAdditionalRenderState.setBlendMode(BlendMode.Alpha)
    newBlock.setMaterial(newMaterial)
    newBlock.setQueueBucket(Bucket.Translucent)
    newBlock.setUserData("phantom", true)
    newBlock
  }

  // TODO: memoise this so that meshes etc are shared for each height
  // TODO: rename this
  private def dirtBox(height: Int) = {
    val boxMesh = new Box(Vector3f.ZERO, Vector3f.UNIT_XYZ)
    val boxGeometry = new Geometry("Shiny box", boxMesh)
    TangentBinormalGenerator.generate(boxMesh)
    boxGeometry.setMaterial(boxMaterial)
    val textureScale = math.pow(2, height).toFloat
    boxGeometry.getMesh.scaleTextureCoordinates(new Vector2f(textureScale, textureScale))
    boxGeometry.setUserData("height", height)
    boxGeometry
  }

  // TODO: refactor to reduce deplicated code
  private def metalBox(height: Int) = {
    val boxMesh = new Box(Vector3f.ZERO, Vector3f.UNIT_XYZ)
    val boxGeometry = new Geometry("Metal box", boxMesh)
    TangentBinormalGenerator.generate(boxMesh)
    boxGeometry.setMaterial(metalMaterial)
    val textureScale = math.pow(2, height).toFloat
    boxGeometry.getMesh.scaleTextureCoordinates(new Vector2f(textureScale, textureScale))
    boxGeometry.setUserData("height", height)
    boxGeometry
  }

  // TODO: rename to dirt
  private lazy val boxMaterial = {
    val boxMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")

    val diffuseTexture = assetManager.loadTexture("Textures/SandPebbles/SandPebblesDiffuse.jpg")
    diffuseTexture.setWrap(Texture.WrapMode.Repeat)
    diffuseTexture.setAnisotropicFilter(8)
    boxMaterial.setTexture("DiffuseMap", diffuseTexture)

    val normalTexture = assetManager.loadTexture("Textures/SandPebbles/SandPebblesNormal.jpg")
    normalTexture.setWrap(Texture.WrapMode.Repeat)
    normalTexture.setAnisotropicFilter(8)
    boxMaterial.setTexture("NormalMap", normalTexture)

    //    val specularTexture = assetManager.loadTexture("Textures/SandPebbles/SandPebblesSpecular.jpg")
    //    specularTexture.setWrap(Texture.WrapMode.Repeat)
    //    specularTexture.setAnisotropicFilter(8)
    //    boxMaterial.setTexture("SpecularMap", specularTexture)

    boxMaterial.setBoolean("UseMaterialColors",true)
    boxMaterial.setColor("Diffuse",ColorRGBA.White)  // minimum material color
    //    boxMaterial.setColor("Specular",ColorRGBA.White) // for shininess
    boxMaterial.setColor("Ambient", ColorRGBA.White)
    boxMaterial
  }

  private lazy val metalMaterial = {
    // TODO: this should not be unshaded, it should use Lighting.j3md
    val boxMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")

    val colourTexture = assetManager.loadTexture("Textures/ScratchedMetal/ScratchedMetal.jpg")
    colourTexture.setAnisotropicFilter(8)
    colourTexture.setWrap(Texture.WrapMode.Repeat)
    boxMaterial.setTexture("ColorMap", colourTexture)
    boxMaterial
  }
}
