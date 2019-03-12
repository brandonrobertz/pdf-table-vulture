/*
package org.bxroberts.tablevulture

import com.snowtide.PDF
import com.snowtide.pdf.Document

import org.scalatest.FunSuite

class Table5ExtractorBaseSuite extends FunSuite {
  val testFile = "./data/DEOCS_table_5.pdf"
  val pdf: Document = PDF.open(testFile)

{
  "title": "Table 5. Respondents' Perceptions of Chain of Command Support",
  "questions": [
    "Promote a unit climate based on “respect and\ntrust”",
    "Refrain from sexist comments and behaviors",
    "Actively discourage sexist comments and behaviors",
    "Provide sexual assault prevention and response\ntraining that interests and engages you",
    "Encourage bystander intervention to assist others\nin situations at risk for sexual assault or other\nharmful behavior",
    "Encourage victims to report sexual assault",
    "Create an environment where victims feel comfortable\nreporting sexual assault"
  ]
}

}

class TableRowCase3Suite extends TableExtractorBase3Suite {
  test("Can parse multiline questions propertly") {
    assert(q1.topText.length > 0)
    assert(q1.bottomText.length > 0)
  }

  test("Can find table row on single page") {
    val table: TableDesc = new TableDesc(
      title,
      Array(q1)
    )
    val rows: Array[TableRow] = te.findTableRows(table)
    println(f"findTableRows rows: ${rows.length}%s")
    assert(rows.length == table.questions.length)
  }

  test("Can find multiple table row on single page") {
    val table: TableDesc = new TableDesc(
      title,
      Array(q1, q2)
    )
    val rows: Array[TableRow] = te.findTableRows(table)
    println(f"findTableRows rows: ${rows.length}%s")
    assert(rows.length == table.questions.length)
  }

  test("Can find rows on single page in random order") {
    val table: TableDesc = new TableDesc(
      title,
      Array(q2, q1)
    )
    val rows: Array[TableRow] = te.findTableRows(table)
    println(f"findTableRows rows: ${rows.length}%s")
    assert(rows.length == table.questions.length)
  }

  test("Can find rows spanning multiple pages") {
    val table: TableDesc = new TableDesc(
      title, Array(q1, q2, q3, q4, q5)
    )
    val rows: Array[TableRow] = te.findTableRows(table)
    println(f"findTableRows rows: ${rows.length}%s")
    assert(rows.length == table.questions.length)
  }

}

class SplitTableRowCase3Suite extends TableExtractorBase3Suite {
  test("Can split a tableRow") {
    val table: TableDesc = new TableDesc(
      title,
      Array(q1, q2)
    )
    val rows: Array[TableRow] = te.findTableRows(table)
    for (i <- 0 to table.questions.length - 1) {
      val q = table.questions(i)
      val cells = te.splitTableRow(q, rows(i))
      println(f"cells length: ${cells.length}%d")
      assert(cells.length == 15)
      val qText = cells(0)
      val val1  = cells(1)
      val val2  = cells(2)
      val valN  = cells(cells.length-1)
      assert(qText contains q1.topText)
      // do a specific check to ensure we get the start and
      // end char correct
      if (i == 0) {
        assert(val1 == "371")
        assert(val2 == "(35%)")
        assert(valN == "(3%)")
      } else if (i == 1) {
        assert(val1 == "381")
        assert(val2 == "(36%)")
        assert(valN == "(2%)")
      }
    }
  }
}

class CSVWriterCase3Suite extends TableExtractorBase3Suite {
  test("Can extract table to CSV") {
    val table: TableDesc = new TableDesc(
      title,
      Array(q1, q2, q3, q4, q5)
    )
    te.extractTable(table)
  }
}
*/
