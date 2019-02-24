package org.bxroberts.tablevulture

import java.lang.System

import com.snowtide.PDF
import com.snowtide.pdf.Document


object PDFTableVulture {
  def main(args: Array[String]) {
    if (args.length != 3) {
      println("PDFTableVulture: Extract CSV tables from annoying PDFs")
      println("USAGE: PDFTableVulture.jar <table_config> <input_pdf> <output_file>")
      System.exit(1)
    }
    def tableConfigFilename: String = args(0)
    def inputPDFFilename: String = args(1)
    def outputCSVFilename: String = args(2)
    val tableDesc = new TableDescReader().loadFile(tableConfigFilename)

    val pdf: Document = PDF.open(inputPDFFilename)
    val te: TableExtractor = new TableExtractor(pdf)
    te.extractTable(tableDesc, outputCSVFilename)
  }
}
