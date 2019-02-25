package org.bxroberts.tablevulture

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

import play.api.libs.json.Json
import play.api.libs.json.JsValue

case class TableDescFmt(title: String, questions: Array[String])

class TableDescReader() {
  def loadFile(filename: String): TableDesc = {
    val jsonText = Source.fromFile(
      filename
    ).getLines.mkString.trim
   return loadText(jsonText)
  }

  def loadText(text: String): TableDesc = {
    val json: JsValue = Json.parse(text)
    val title = (json \ "title").as[String]
    println(f"Loaded table desc title: ${title}%s")
    val questions: Array[String] = (json \ "questions").as[Array[String]]
    var tableQs: ArrayBuffer[TableQuestion] = ArrayBuffer[TableQuestion]()
    for (q <- questions) {
      println(f"Parsing question: ${q}%s")
      tableQs += new TableQuestion(q)
    }
    val table: TableDesc = new TableDesc(
      title, tableQs.toArray
    )
    return table
  }
}
