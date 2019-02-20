package org.bxroberts.tablevulture

import scala.util.control.Breaks._
import collection.mutable.HashMap

import java.io.StringWriter

import com.snowtide.PDF
import com.snowtide.pdf.Document
import com.snowtide.pdf.Page
import com.snowtide.pdf.VisualOutputTarget
import com.snowtide.pdf.OutputTarget

// import org.bxroberts.interactivescraper.ManualCrawl

class Coord(_x: Float, _y: Float) {
  def x = _x
  def y = _y
}

class Primitives(filename: String) {
  def pdf: Document = PDF.open(filename)

  def extractPageText(pg: Int, x: Float, y: Float): String = {
    var page: Page = pdf.getPage(pg)
    var buffer = new StringWriter
    page.pipe(new OutputTarget(buffer))
    return buffer.toString();
  }

  def findTableTop(title: String): Coord = {
    // see if it matches
    // return v if yes
    var c = new Coord(1.0f, 2.0f)
    return c
  }

  /**
   * Starting from the top of the given page, scan down, line-by-line,
   * until the text from the current line matches some condition.
   * The condition function needs to accept a text string and return
   * false (don't stop) or true (condition met, stop scanning).
   */
  def xScanUntil(condition: (String) => Boolean): Float = {
    // TODO: find page top
    var v: Float = 100f
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
  def yScanUntil(condition: (String) => Boolean): Float = {
    var v: Float = 0f
    // TODO: find page top
    var xMax: Float = 100.0f
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
}

class TableExtractor(filename: String) {
  def tools = new Primitives(filename)

  def identifyPage(text: String): Int = {
    var lastPage: Int = 100
    var currentPage: Int = 1
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
    var page = 17
    var primitives = new Primitives(filename)
    var pageText: String = primitives.extractPageText(page, 0f, 0f)
    println(pageText)
    println("==================================================")
  }
}

