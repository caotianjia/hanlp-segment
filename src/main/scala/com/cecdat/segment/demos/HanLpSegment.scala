package com.cecdat.segment.demos

import java.io._

import com.cecdat.segment.corpus.MedicineReport
import com.cecdat.segment.loader.DictionaryLoader
import com.fasterxml.jackson.databind.ObjectMapper

import scala.io.Source

object HanLpSegment {

  def main(args: Array[String]): Unit = {

    DictionaryLoader("dict/disease.txt", "ndisease")
    DictionaryLoader("dict/drug.txt", "ndrug")
    DictionaryLoader("dict/symptom.txt", "nsymptom")

    val esIndex = "index"
    val esIndexType = "type"

    val objectMapper = new ObjectMapper()
    val file = new File("source/test.txt")
    val source = Source.fromInputStream(new FileInputStream(file))
    val saveFile = "es1.txt"
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), "utf-8"))

    source.getLines().foreach(line => {
      val document = objectMapper.readTree(line)
      val report = MedicineReport(document)
      val content = report.getElasticSearchIndexCommandJson(esIndex, esIndexType) + "\n" + report.toJson()
      writer.write(content)
      writer.newLine()
    })
    writer.close()
  }

}
