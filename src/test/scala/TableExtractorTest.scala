package org.bxroberts.tablevulture

import com.snowtide.PDF
import com.snowtide.pdf.Document

import org.scalatest.FunSuite

class TableExtractorSuite extends FunSuite {
  var testFile = "data/DEOCS.pdf"
  var pdf: Document = PDF.open(testFile)
  var t = new TableExtractor(pdf)

  test("Can identify the correct page") {
    var tableTitle = "Table 2.13 Sexual Assault Prevention Climate"
    var pageNumber = t.identifyPage(tableTitle)
    assert(pageNumber == 17)
  }

  test("Can identify another correct page") {
    var tableTitle = "Figure 7. Respondents’ Knowledge of Military Attorney Eligibility."
    var pageNumber = t.identifyPage(tableTitle)
    assert(pageNumber == 21)
  }
}
