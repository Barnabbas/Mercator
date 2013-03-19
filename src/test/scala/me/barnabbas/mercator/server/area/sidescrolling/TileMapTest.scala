package me.barnabbas.mercator.server.area.sidescrolling

import org.scalatest.FunSuite

class TileMapTest extends FunSuite {
  
	// todo: create a new test for TileMap
	test("TileMapTest")(pending)
  
//  test("isObstalce"){
//    val tiles = Map((0, 0, 0) -> Open, (1, 1, 0) -> Obstacle, (0, 2, 1) -> Event(_ => ???))
//    val tileMap = TileMap(tiles, 1, 1, 1)
//    
//    // no longer used
////    assert(tileMap.isObstacle(0, 0, 0) === false)
////    assert(tileMap.isObstacle(1, 1, 0) === true)
////    assert(tileMap.isObstacle(0, 2, 1) === false)
//    assert(tileMap(0, 0, 0) === Open)
//    intercept[Exception](tiles(0, 3, 1))
//  }
//  
//  test("baseTiles"){
//    val tiles = TileMap.baseTiles(10, 10, 5)
//    // some standard boundary analyses
//    
//    // for x
//    def assertX(x: Int, out: Tile) = assert(tiles(x, 11, 4) === out, s"test failed for x = $x")
//    assertX(-1, Obstacle)
//    assertX(0, Open)
//    assertX(5, Open)
//    assertX(9, Open)
//    assertX(10, Obstacle)
//    
//    // for y
//    def assertY(y: Int, out: Tile) = assert(tiles(5, y, 4) === out, s"test failed for y = $y")
//    assertY(9, Obstacle)
//    assertY(10, Open)
//    assertY(11, Open)
//    assertY(30, Open)
//    
//    // for z
//    def assertZ(z: Int, out: Tile) = assert(tiles(5, 11, z) === out, s"test failed for z = $z")
//    assertZ(-1, Obstacle)
//    assertZ(0, Open)
//    assertZ(3, Open)
//    assertZ(4, Open)
//    assertZ(5, Obstacle)
//  }

}