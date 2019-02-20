package org.bxroberts.tablevulture

import scala.util.control.Breaks._
import collection.mutable.HashMap

import java.io.StringWriter

import com.snowtide.PDF
import com.snowtide.pdf.Document
import com.snowtide.pdf.Page
import com.snowtide.pdf.VisualOutputTarget
import com.snowtide.pdf.RegionOutputTarget

/**
 * A single point on a PDF page
 */
class Coord(_x: Int, _y: Int) {
  def x = _x
  def y = _y
}

/**
 * A box for a region of a PDF page.
 */
class Box(_x: Int, _y: Int, _w: Int, _h: Int) {
  def x = _x
  def y = _y
  def w = _w
  def h = _h
  override def toString(): String = {
    return f"Box x:${x}%d, y:${y}%d, w:${w}%d, h:${h}%d"
  }
}

class Size(_w: Int, _h: Int) {
  def w = _w
  def h = _h
}

class Primitives(pdf: Document) {
  def pages: Int = pdf.getPageCnt

  /**
   * Return a Size with the page width and height
   * filled. Note that in snowtide PDF model, a
   * PDF coordinate system works like this:
   *
   * (0,792)_____ (612,792)
   *       |     |
   *       |     |
   *       |     |
   *       |_____|
   *    (0,0)    (612,0)
   */
  def pageSize(pg: Int): Size = {
    var page = pdf.getPage(pg)
    var w = page.getPageWidth
    var h = page.getPageHeight
    return new Size(w, h)
  }

  /**
   * For a given page, extract its text content using
   * the visual output target handler, which attempts
   * to use spacing to represent the visual layout
   * of the text on a page.
   */
  def extractPageText(pg: Int): String = {
    var page: Page = pdf.getPage(pg)
    var buffer = new StringWriter
    page.pipe(new VisualOutputTarget(buffer))
    return buffer.toString();
  }

  /**
   * For a given page and region on that page, extract and
   * return the text in that box.
   */
  def boxText(pg: Int, box: Box, label: String = "region"): String = {
    var tgt: RegionOutputTarget = new RegionOutputTarget()
    tgt.addRegion(box.x, box.y, box.w, box.h, label)

    var page = pdf.getPage(pg)
    page.pipe(tgt)

    var region: String = tgt.getRegionText(label)
    return region
  }

  /**
   * Starting from the top of the given page, scan down, line-by-line,
   * until the text from the current line matches some condition.
   * The condition function needs to accept a text string and return
   * false (don't stop) or true (condition met, stop scanning).
   *
   * Returns the y position of the top of the text or -1.
   */
  def yScanUntil(
    pg: Int, condition: (String) => Boolean, startY: Int = -1
  ): Int = {
    // start at Y or the top (the height) of the page, work down
    var y: Int = startY
    var size: Size = pageSize(pg)
    if (startY == -1) {
      y = size.h
    }
    // use a very thin scan line to get accurate results
    var scanLineSize = 1
    breakable {
      while (y >= 0) {
        // extract text from a thin line across the page
        var box = new Box(0, y, size.w, scanLineSize)
        var text = boxText(pg, box).replace("\n", "")
        println(f"yScanUntil at ${box.toString}%s text: ${text}%s")
        if (condition(text)) break
        // increment and loop otherwise
      y = y - 1
      }
    }
    return y;
  }

  /**
   * For a give page and y position, scan the x-axis (from
   * right to left) until a condition function returns true.
   */
  def xScanUntil(
    pg: Int, condition: (String) => Boolean, y: Int, startX: Int = -1
  ): Int = {
    var scanLineSize = 1
    var size: Size = pageSize(pg)
    var x = startX;
    if (startX == -1) {
      x = size.w - 1
    }
    breakable {
      while (x >= 0) {
        var box = new Box(x, y, size.w, scanLineSize)
        var text = boxText(pg, box).replace("\n", "")
        println(f"xScanUntil at ${box.toString}%s text: ${text}%s")
        if (condition(text)) break
        x = x - 1
      }
    }
    return x;
  }

  /**
   * Find the coordinates of a given text on a page. It does so using
   * two methods for the x and y axes.
   *
   * 1) To find the Y (vertical position of the text) we scan down
   * from the top of the page with a very thin scanline, grabbing
   * the text we find. We mark the initial spot we find the text
   * we're looking for.
   *
   * 2) Then, once we have the Y position, we scan the X axis
   * from right to left, looking for the first instance of text
   * match and then stopping.
   *
   * This gives us propert X and Y values for our text.
   */
  def findText(pg: Int, title: String): Coord = {
    def stringFound(text: String): Boolean = {
      println(f"stringFound? text: ${text}%s")
      return text contains title
    }
    var y = yScanUntil(pg, stringFound)

    var lastText = ""
    def stringCaptured(text: String): Boolean = {
      println(f"stringCaptured? text: ${text}%s")
      if (lastText.isEmpty) {
        lastText = text
        return false
      }
      return  text contains title;
   }
    var x = xScanUntil(pg, stringCaptured, y)

    return new Coord(x, y)
  }

  /**
   * Take our title and make some modifications
   */
  def regexify(string: String): String = {
    var replaced =  string.replaceAll(
      "\\s+", "\\\\s*"
    ).replaceAll(
      "’", "."
    )
    return f".*${replaced}%s.*"
  }

  /**
   * For a given text, find the first page it appears on.
   */
  def identifyPage(text: String): Int = {
    var ptrn = regexify(text)
    for (pg <- 1 to pages - 1) {
      // Extract page text
      var pageText = extractPageText(pg)
      println("================================================")
      println(f"page: ${pg}%d text: ${text}%s ptrn: ${ptrn}%s")
      println(f"pageText:\n${pageText}%s")
      println("------------------------------------------------")

      if (pageText contains text) {
        println("Page text exact match!")
        return pg
      }
      if (pageText.replace("\n", "") matches ptrn) {
        println("Pattern match!")
        return pg
      }
    }

    return -1
  }
}

/**
 * A description of a table we're intending on extracting.
 */
class TableDesc(_title: String, _questions: Array[String]) {
  def title = _title
  def questions = _questions
}

/**
 * All the things we need for table extaction.
 */
class TableExtractor(pdf: Document) {
  def p = new Primitives(pdf)

  /**
   * The algorithm for extracting tables:
   *
   * 1. Find the Y of table title
   *
   * 2. From that Y, for each Q in Questions ...
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
    var pg: Int = p.identifyPage(table.title)
    var titleY = p.findText(pg, table.title)
    return "String"
  }
}

object PDFTableVulture {
  def main(args: Array[String]) {
    println("==================================================")
    var filename = "data/DEOCS.pdf"
    var tableName = "Table 2.13 Sexual Assault Prevention Climate"
    var page = 17
    var pdf: Document = PDF.open(filename)
    var primitives = new Primitives(pdf)
    var pageText: String = primitives.extractPageText(page)
    // println(pageText)
    var box = new Box(200, 200, 100, 50)
    var boxText = primitives.boxText(page, box)
    println(boxText)
    println("--------------------------------------------------")
  }
}

