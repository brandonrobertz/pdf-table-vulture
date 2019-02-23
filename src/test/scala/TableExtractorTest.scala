package org.bxroberts.tablevulture

import com.snowtide.PDF
import com.snowtide.pdf.Document

import org.scalatest.FunSuite

class TableExtractorBaseSuite extends FunSuite {
  val testFile = "data/DEOCS.pdf"
  val pdf: Document = PDF.open(testFile)
  val title = "Table 2.14 Sexual Assault Response Climate"
  val q1 = new TableQuestion("""
If a coworker were to report a
sexual assault, my chain of
command/supervision would take
the report seriously.
""")
  val q2 = new TableQuestion("""
If a coworker were to report a
sexual assault, my chain of
command/supervision would keep
the knowledge of the report limited
to those with a need to know.
""")
  // this one is on the next page!
  val q3 = new TableQuestion("""
If a coworker were to report a
sexual assault, my chain of
command/supervision would
discourage military members or
employees from spreading rumors
and speculation about the
allegation.
""")
  val q4 = new TableQuestion("""
If a coworker were to report a
sexual assault, my chain of
command/supervision would
promote healthcare, legal, or other
support services to the reporter.
""")
  val q5 = new TableQuestion("""
If a coworker were to report a
sexual assault, my chain of
command/supervision would
support the reporter for speaking
up.
""")
  val te: TableExtractor = new TableExtractor(pdf)
}

class TableRowSuite extends TableExtractorBaseSuite {
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

class SplitTableRowSuite extends TableExtractorBaseSuite {
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
        assert(val1 == "6")
        assert(val2 == "(4%)")
        assert(valN == "(49%)")
      } else if (i == 1) {
        assert(val1 == "5")
        assert(val2 == "(3%)")
        assert(valN == "(42%)")
      }
    }
  }
}
