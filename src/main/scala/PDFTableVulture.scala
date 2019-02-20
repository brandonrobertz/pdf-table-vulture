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
}

class Size(_w: Int, _h: Int) {
  def w = _w
  def h = _h
}

class Primitives(filename: String) {
  def pdf: Document = PDF.open(filename)
  def pages: Int = pdf.getPageCnt

  /**
   * Return a Size with the page width and height
   * filled.
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
  def extractPageText(pg: Int, x: Int, y: Int): String = {
    var page: Page = pdf.getPage(pg)
    var buffer = new StringWriter
    page.pipe(new VisualOutputTarget(buffer))
    return buffer.toString();
  }

  /**
   * For a given page and region on that page, extract and
   * return the text in that area.
   */
  def areaText(pg: Int, area: Box, label: String = "region"): String = {
    var tgt: RegionOutputTarget = new RegionOutputTarget()
    tgt.addRegion(area.x, area.y, area.w, area.h, label)

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
  def xScanUntil(condition: (String) => Boolean): Int = {
    // TODO: find page top
    var v: Int = 100
    breakable {
      while (v < 0) {
        // extract text from a thin line across the page
        var text = "Extracted from line"
        if (condition(text)) break
        // increment and loop otherwise
        v = v - 1
      }
    }
    return v;
  }

  // TODO: merge into xScanUntil
  def yScanUntil(condition: (String) => Boolean): Int = {
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

  /**
   * Find the coordinates of a given text on a page.
   */
  def findText(pg: Int, title: String): Coord = {
    def stringFound(text: String): Boolean = {
      return true
    }
    var x = xScanUntil(stringFound)
    return new Coord(x, 0)
  }

}

class TableExtractor(filename: String) {
  def tools = new Primitives(filename)

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
    var primitives = new Primitives(filename)
    var pageText: String = primitives.extractPageText(page, 0, 0)
    // println(pageText)
    var area = new Box(200, 200, 100, 50)
    var areaText = primitives.areaText(page, area)
    println(areaText)
    println("==================================================")
  }
}

