package com.cecdat.segment.corpus

import com.fasterxml.jackson.databind._

import scala.collection.JavaConverters._

class ElasticsearchObject(val id: String, val fileName: String, val createdDate: Long, val sfzh: String, val createdBy: String, val name: String, val duns: String, val url: String, val content: String) {

  import ElasticsearchObject._

  def toJson(): String = {
    val obj = Map[String, Any](
      "id" -> id,
      "fileName" -> fileName,
      "createdDate" -> createdDate,
      "sfzh" -> sfzh,
      "createdBy" -> createdBy,
      "name" -> name,
      "duns" -> duns,
      "url" -> url,
      "content" -> content
    )
    mapper.writeValueAsString(obj.asJava)
  }

  def getElasticSearchIndexCommandJson(index: String, indexType: String): String = {
    val obj = Map[String, Any](
      "_index" -> index,
      "_type" -> indexType,
      "_id" -> id
    ).asJava
    val delete = mapper.writeValueAsString(Map[String, Any]("delete" -> obj).asJava)
    val create = mapper.writeValueAsString(Map[String, Any]("create" -> obj).asJava)
    s"$delete\n$create"
  }
}

object ElasticsearchObject {

  private val mapper = new ObjectMapper()

  def apply(json: JsonNode, id: String): ElasticsearchObject = {
    val fileName = json.get("fileName").asText().trim
    val createdDate = json.get("createdDate").asLong()
    val sfzh = json.get("sfzh").asText().trim
    val createdBy = json.get("createdBy").asText().trim
    val name = json.get("name").asText().trim
    val duns = json.get("duns").asText().trim
    val url = json.get("url").asText().trim
    val content = json.get("content").asText().trim
    new ElasticsearchObject(id, fileName, createdDate, sfzh, createdBy, name, duns, url, content)
  }

}
