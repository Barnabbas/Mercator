package me.barnabbas.mercator.server.area.sidescrolling

import org.scalatest.FunSuite

/**
 * Testing the static functions of Tile
 * @author Barnabbas
 */
class TileTest extends FunSuite {
  
  import Tile._
  
  test("toTiles") {
    val bools = Map((0, 0, 0) -> false, (-2, 3, 900) -> false) withDefaultValue true
    val tiles = toTiles(bools)
    
    // checking the false values
    assert(tiles(0, 0, 0) === Open)
    assert(tiles(-2, 3, 900) === Open)
    
    // obstacle values
    assert(tiles(1, 0, 0) == Obstacle)
    assert(tiles(1, 1, 1) == Obstacle)
    assert(tiles(0, 0, -1) == Obstacle)
  }

  test("baseTiles") {
    val tiles = baseTiles(10, 10, 5)
    // some standard boundary analyses

    // for x
    def assertX(x: Int, out: Tile) = assert(tiles(x, 11, 4) === out, s"test failed for x = $x")
    assertX(-1, Obstacle)
    assertX(0, Open)
    assertX(5, Open)
    assertX(9, Open)
    assertX(10, Obstacle)

    // for y
    def assertY(y: Int, out: Tile) = assert(tiles(5, y, 4) === out, s"test failed for y = $y")
    assertY(9, Obstacle)
    assertY(10, Open)
    assertY(11, Open)
    assertY(30, Open)

    // for z
    def assertZ(z: Int, out: Tile) = assert(tiles(5, 11, z) === out, s"test failed for z = $z")
    assertZ(-1, Obstacle)
    assertZ(0, Open)
    assertZ(3, Open)
    assertZ(4, Open)
    assertZ(5, Obstacle)
  }
}
