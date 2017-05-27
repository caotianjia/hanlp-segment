package com.cecdat.segment.demos

import java.io._

import com.cecdat.segment.corpus.ElasticsearchObject
import com.fasterxml.jackson.databind.ObjectMapper

object FileContentExtractor {

  val objectMapper = new ObjectMapper()

  def main(args: Array[String]): Unit = {

    val sourceDir = if (args.indexOf("--source") >= 0) args(args.indexOf("--source") + 1) else "/Users/tianjia/Workspace/spark_segment/dataTest"
    val destDir = if (args.indexOf("--dest") >= 0) args(args.indexOf("--dest") + 1) else "/Users/tianjia/Desktop/result/"

    val files = subDir(new File(sourceDir))

    for (file <- files) {
      extract(file, destDir)
    }
  }

  private def subDir(dir: File): Iterator[File] = {
    val d = dir.listFiles.filter(_.isDirectory)
    val f = dir.listFiles.filter(_.isFile).toIterator
    f ++ d.toIterator.flatMap(subDir _)
  }

  private def extract(file: File, destDir: String): Unit = {
    val json = objectMapper.readTree(file).findPath("hits").findPath("hits")
    val saveFile = destDir + file.getName
    println(saveFile)
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), "utf-8"))

    if (json.isArray) {
      for (i <- 0 until json.size()) {
        val id = json.get(i).findPath("_id").asText().trim
        val content = json.get(i).findPath("_source")
        val elasticsearchObject = ElasticsearchObject(content, id)
        val str = elasticsearchObject.toJson()
        writer.write(str)
        writer.newLine()
      }
    }
    writer.close()
  }

}
