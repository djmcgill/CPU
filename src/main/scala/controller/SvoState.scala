package controller

import logic.voxels.{SVO, SvoNavGrid}

trait SvoState extends GameState {
  def maxHeight: Int = app.getRootNode.getUserData("maxHeight")
  def maxHeight_= (height: Int): Unit = app.getRootNode.setUserData("maxHeight", height)
  
  def svo: SVO = app.getRootNode.getUserData("svo")
  def svo_= (newSVO: SVO) = app.getRootNode.setUserData("svo", newSVO)

  def svoNavGrid: SvoNavGrid = app.getRootNode.getUserData("navGrid")
  def svoNavGrid_= (newSvoNavGrid: SvoNavGrid): Unit = app.getRootNode.setUserData("navGrid", newSvoNavGrid)
}
