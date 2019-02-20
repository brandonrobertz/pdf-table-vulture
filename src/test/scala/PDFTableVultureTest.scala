package org.bxroberts.tablevulture

import org.scalatest.FunSuite

class PDFTableVultureSuite extends FunSuite {

  test("Can instantiate Primitives with filename") {
    val p = new Primitives("data/DEOCS.pdf")
    assert(p.pages > 0)
  }
}

