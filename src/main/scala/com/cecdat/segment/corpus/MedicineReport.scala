package com.cecdat.segment.corpus

import java.util.Date

import com.cecdat.segment.data.RegionCodes
import com.fasterxml.jackson.databind._
import com.hankcs.hanlp.HanLP
import org.apache.commons.lang3.time.DateFormatUtils

import scala.collection.JavaConverters._

class MedicineReport(val id: String, val createdDate: String, val createdBy: String, val name: String, val gender: String, val content: String, val category: String, val region: String, val idCard: String) {

  import MedicineReport._

  val segmentResults = HanLP.segment(content)

  val disease = segmentResults.asScala.toList.filter(_.nature.name() == "ndisease").map(_.word)
  val drug = segmentResults.asScala.toList.filter(_.nature.name() == "ndrug").map(_.word)
  val symptom = segmentResults.asScala.toList.filter(_.nature.name() == "nsymptom").map(_.word)

  def toJson(): String = {
    var obj = Map[String, Any](
      "id" -> id,
      "createdBy" -> createdBy,
      "name" -> name,
      "content" -> content,
      "category" -> category,
      "createdDate" -> createdDate,
      "disease" -> disease.asJava,
      "drug" -> drug.asJava,
      "symptom" -> symptom.asJava
    )

    if (!region.isEmpty) obj += ("region" -> region)
    if (!gender.isEmpty) obj += ("gender" -> gender)
    if (!idCard.isEmpty) obj += ("idCard" -> idCard)

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

object MedicineReport {

  private val mapper = new ObjectMapper()

  def apply(json: JsonNode): MedicineReport = {
    val id = json.get("id").asText().trim
    val createdBy = json.get("createdBy").asText().trim
    val ssn = json.get("sfzh").asText().trim
    val gender = getGender(ssn)
    val idCard = getIdCard(ssn)
    val region = getRegion(ssn)
    val name = json.get("name").asText().trim
    val content = json.get("content").asText().trim
    val createdDate = DateFormatUtils.format(json.get("createdDate").asLong(), "yyyy-MM-dd")
    val category = getCategory(name)
    new MedicineReport(id, createdDate, createdBy, name, gender, content, category, region, idCard)
  }

  private def getCategory(name: String) = {
    if (name.contains("处方")) {
      "处方"
    } else if (name.contains("化验单")) {
      "化验单"
    } else if (name.contains("出院小结")) {
      "出院小结"
    } else {
      "其它"
    }
  }

  private def getGender(ssn: String): String = {
    if (ssn != null && ssn.length == 18) {
      val sexInt = ssn.substring(16, 17).toInt
      if (0 == sexInt % 2)
        "女"
      else "男"
    } else ""
  }

  private def getIdCard(ssn: String): String = {
    if (ssn != null && ssn.length == 18) {
      ssn
    } else ""
  }

  private def getCurrentAge(ssn: String): String = {
    if (ssn != null && ssn.length == 18) {
      val birthYear = ssn.substring(6, 10).toInt
      val now: Date = new Date()
      val yearInt = DateFormatUtils.format(now, "yyyy").toInt
      val age = yearInt - birthYear
      age + ""
    } else "0"
  }

  private def getRegion(ssn: String): String = {
    if (ssn != null && ssn.length == 18)
      RegionCodes.getRegion(ssn.substring(0, 6))
    else ""
  }

}
