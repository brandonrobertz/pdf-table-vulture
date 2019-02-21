package org.bxroberts.tablevulture

import com.snowtide.pdf.Document

/**
 * A survey question object.
 */
class TableQuestion(_text: String) {
  def text = _text
  private def lines = _text.split("\n").map(_.trim).filter(!_.isEmpty)
  def topText(): String = {
    val top = lines(0)
    return top
  }
  def bottomText(): String = {
    val btm = lines(lines.length-1)
    return btm
  }
}

/**
 * A description of a table we're intending on extracting.
 */
class TableDesc(_title: String, _questions: Array[TableQuestion]) {
  def title = _title
  def questions = _questions
}

/**
 * Table row. Holds box and contents of row.
 * NOTE: We may need to make pg and box arrays
 * in case the question spans more than one page.
 */
class TableRow(_pg: Int, _box: Box) {
  def pg = _pg
  def box = _box
  override def toString(): String = {
    return f"TableRow pg: ${pg}%d, ${box.toString}%s"
  }
}

/**
 * All the things we need for table extaction.
 */
class TableExtractor(pdf: Document) {
  def p = new Primitives(pdf)

  /**
   * For a given table description, find the TableRows
   * identifying each row in the table.
   */
  def findTableRows(table: TableDesc): Array[TableRow] = {
    var pg: Int = p.identifyPage(table.title)
    val titleCoord: Coord = p.findText(pg, table.title)
    var pgSize: Size = p.pageSize(pg)

    println(f"findTableRows pg: ${pg}%d")
    println(f"titleCoord: ${titleCoord.toString}%s, pgSize: ${pgSize.toString}%s")
    println(f"Number of questions: ${table.questions.length}%d")

    // Identify our table's question row blocks
    // TODO: This search needs to span pages.
    var rows: Array[TableRow] = Array.empty
    for (question <- table.questions) {
      println("============================================================")
      println(f"Finding question top: ${question.topText}%s")
      val topCoord = p.findText(
        pg, question.topText, 0, startY=titleCoord.y-1
      )

      // use the topY as start (not topY-1) in case we have
      // single line question
      println("============================================================")
      println(f"Finding question bottom: ${question.bottomText}%s")
      val btmCoord = p.findText(
        pg, question.bottomText, 0, startY=topCoord.y
      )

      // build a box using our two Y coords, assume 100% width
      println("============================================================")
      println("Building box")
      val box = new Box(
        0, topCoord.y, pgSize.w, topCoord.y - btmCoord.y
      )
      println(f"box: ${box.toString}%s")

      println("============================================================")
      println("Building tableRow")
      val tableRow = new TableRow(pg, box)
      println(f"tableRow: ${tableRow.toString}%s")
      rows ++= Array(tableRow)
    }

    return rows
  }

  /**
   * The algorithm for extracting tables:
   *
   * 1. Find the Y of table title
   *
   * 2. From that Y, for each Q in TableQuestions ...
   *   a. find the top of the first question
   *   b. find the bottom of the first question
   *
   * NOTE: All searches wrap to the next page
   *
   * 3. Take our question rows and for each
   * split the question from the values
   *
   * 4. Taking advantage of the 9 (10%) pattern
   * of the values (use a config), split the
   * values N times
   *
   * 5. This gives us a multi dimensional array
   * that we need to turn into a CSV
   */
  def extractTable(table: TableDesc): String = {
    val tableRows: Array[TableRow] = findTableRows(table)
    for (row <- tableRows) {
    }

    return "String"
  }
}
