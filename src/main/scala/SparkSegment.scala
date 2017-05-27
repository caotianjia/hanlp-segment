import SparkSegment._
import com.cecdat.segment.corpus.MedicineReport
import com.cecdat.segment.loader.DictionaryLoader
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.spark._

object Spark {
  val conf = new SparkConf().setAppName("Spark Segment Application")
  val sc = new SparkContext(conf)
}

object SparkSegment {

  var sourceDir = String.format("/Users/tianjia/Workspace/hanlp-segment/source")
  var destDir = String.format("/Users/tianjia/Workspace/hanlp-segment/output")

  val objectMapper = new ObjectMapper();
  val es_index = "shanxi"
  val es_type = "dashuju"

  def main(args: Array[String]): Unit = {

    checkParams(args)

    DictionaryLoader("dict/disease.txt", "ndisease")
    DictionaryLoader("dict/drug.txt", "ndrug")
    DictionaryLoader("dict/symptom.txt", "nsymptom")

    new Calculation(sourceDir, destDir).process()
  }

  private def checkParams(args: Array[String]): Unit = {
    for (i <- 0 until args.size - 1 if args(i) != null) {
      args(i) match {
        case "--source" => sourceDir = String.format(args(i + 1))
        case "--dest" => destDir = String.format(args(i + 1))
        case _ =>
      }
    }
  }

}

class Calculation(sourceDir: String, destDir: String) extends java.io.Serializable {

  def process(): Unit = {
    val fileRDD = Spark.sc.textFile(sourceDir)
    val segmentRDD = fileRDD.filter(_ != "").map(segment).filter(_ != "")
    segmentRDD.saveAsTextFile(destDir)
  }

  val segment = (line: String) => {
    val document = objectMapper.readTree(line)
    val report = MedicineReport(document)
    val content = report.getElasticSearchIndexCommandJson(es_index, es_type) + "\n" + report.toJson()
    content
  }

}