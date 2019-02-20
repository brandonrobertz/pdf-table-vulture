package org.bxroberts.tablevulture

import scala.util.control.Breaks._
import collection.mutable.HashMap

import java.io.StringWriter

import com.snowtide.PDF
import com.snowtide.pdf.Document
import com.snowtide.pdf.Page
import com.snowtide.pdf.VisualOutputTarget
import com.snowtide.pdf.RegionOutputTarget

// import org.bxroberts.interactivescraper.ManualCrawl

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
   */
  def yScanUntil(pg: Int, condition: (String) => Boolean): Int = {
    var start: Size = pageSize(pg)
    // start at the top (the height) of the page, work down
    var y: Int = start.h
    // use a very thin scan line to get accurate results
    var scanLineSize = 1
    var size: Size = pageSize(pg)
    breakable {
      while (y > 0) {
        // extract text from a thin line across the page
        var box = new Box(0, y, size.w, scanLineSize)
        println(f"yScanUntil at ${box.toString}%s")
        var text = boxText(pg, box)
        println(f"text: ${text}%s")
        if (condition(text)) break
        // increment and loop otherwise
        y = y - 1
      }
    }
    return y;
  }

  /*
  def xScanUntil(condition: (String) => Boolean): Int = {
    var v: Int = 0
    // TODO: find page top
    var xMax: Int = 100
    breakable {
      while (v < xMax) {
        break
        // extract text from a thin line across the page
        // see if it matches
        // return v if yes
        // increment and loop otherwise
        v = v + 1
      }
    }
    return v;
  }
  */

  /**
   * Find the coordinates of a given text on a page.
   */
  def findText(pg: Int, title: String): Coord = {
    def stringFound(text: String): Boolean = {
      println(f"stringFound? text: ${text}%s")
      return text contains title
    }
    var y = yScanUntil(pg, stringFound)
    return new Coord(0, y)
  }

}

class TableExtractor(pdf: Document) {
  def tools = new Primitives(pdf)

  /**
   * For a given text, find the first page it appears on.
   */
  def identifyPage(text: String): Int = {
    var lastPage: Int = 100
    var currentPage: Int = 0
    while (currentPage < lastPage) {
      // Extract page text
      // look for page text
      // return if found
      // otherwise, increment page and loop
      currentPage = currentPage + 1
    }
    // return -1
    return -1
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
    println("==================================================")
  }
}

