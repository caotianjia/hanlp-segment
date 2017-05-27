name := "hanlp-segment"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.1.1" % "provided",
  "org.apache.commons" % "commons-lang3" % "3.5",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.7.6",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.6",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.7.6",
  "org.scalatest" % "scalatest_2.11" % "3.0.3",
  "com.hankcs" % "hanlp" % "portable-1.3.4"
)
