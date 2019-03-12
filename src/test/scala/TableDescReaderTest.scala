package org.bxroberts.tablevulture

import org.scalatest.FunSuite

class TableDescReaderSuite extends FunSuite {
  test("Can parse table desc from JSON text") {
    val text = """
      {
        "title": "This is a header",
        "questions":["one", "two"],
        "nValues": 14
      }
    """
    val table = new TableDescReader().loadText(text)
    assert(table.title == "This is a header")
    assert(table.questions.length == 2)
    assert(table.questions(0).text == "one")
    assert(table.questions(1).text == "two")
  }
}
