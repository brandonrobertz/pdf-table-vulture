package org.bxroberts.tablevulture

import com.snowtide.PDF
import com.snowtide.pdf.Document

import org.scalatest.FunSuite

class PrimitivesSuite extends FunSuite {
  var testFile = "data/DEOCS.pdf"
  var pdf: Document = PDF.open(testFile)
  val p = new Primitives(pdf)
  var tablePage = 17
  var tableTitle = "Table 2.13 Sexual Assault Prevention Climate"

  test("Can instantiate Primitives with filename") {
    assert(p.pages > 0)
  }

  test("Can extract text from page") {
    var text = p.extractPageText(tablePage)
    assert(text contains tableTitle)
  }

  test("Can extrct text from a small region on the page") {
    var box = new Box(200, 200, 100, 50)
    var found: String = p.boxText(tablePage, box)
    println(f"x: ${box.x}%d y: ${box.y}%d")
    println(f"w: ${box.w}%d h: ${box.h}%d")
    println(f"Found: ${found}%s")
    assert(found contains "5 (3%)")
    assert(found contains "2 (1%)")
  }

  test("Can find text on page") {
    var c: Coord = p.findText(tablePage, tableTitle)
    println( f"${tableTitle}%s found at x: ${c.x}%d y: ${c.y}%d")
    assert(c.y == 683)
    assert(c.x == 207)
  }
}
