package org.bxroberts.tablevulture

import com.snowtide.PDF
import com.snowtide.pdf.Document

import org.scalatest.FunSuite

class TableExtractorBase3Suite extends FunSuite {
  val testFile = "./data/DEOCS_2nd_Marine_Division_Roll-up.pdf"
  val pdf: Document = PDF.open(testFile)
  val title = "Table 2.16 Sexual Harassment Retaliation Climate"
  val q1 = new TableQuestion("""
In my work group, military
members or employees who file a
sexual harassment complaint would
be excluded from social interactions
or conversations.
""")
  val q2 = new TableQuestion("""
In my work group, military
members or employees who file a
sexual harassment complaint would
be subjected to insulting or
disrespectful remarks or jokes.
""")
  val q3 = new TableQuestion("""
In my work group, military
members or employees who file a
sexual harassment complaint would
be blamed for causing problems.
""")
  val q4 = new TableQuestion("""
In my work group, military
members or employees who file a
sexual harassment complaint would
be denied career opportunities.
""")
  val q5 = new TableQuestion("""
In my work group, military
members or employees who file a
sexual harassment complaint would
be disciplined or given other
corrective action.
""")
  val q6 = new TableQuestion("""
In my work group, military
members or employees who file a
sexual harassment complaint would
be discouraged from moving
forward with the complaint.
""")
  val te: TableExtractor = new TableExtractor(pdf)
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
