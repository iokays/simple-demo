package com.iokays.spark.basic

import org.apache.spark.annotation.InterfaceStability
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

object SparkSessionIT {

  val logger = LoggerFactory.getLogger("SparkSessionIT")

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("SparkSessionIT").master("local[4]").getOrCreate()
    val sc = spark.sparkContext
    val textFile = sc.textFile("src/main/resources/spark.txt")
    logger.info("textFile, count: {}, first: {}", textFile.count(), textFile.first())
    val linesWithSpark = textFile.filter(line => line.contains("Spark"))
    linesWithSpark.cache()
    logger.info("linesWithSpark: {}", linesWithSpark.count())
    val max = textFile.map(line => line.split(" ").size).reduce((a, b) => Math.max(a, b))
    logger.info("max: {}", max)

    val wordCount = textFile.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b)
    wordCount.cache()
    logger.info("wordCount.map: {}", textFile.flatMap(line => line.split(" ")).map(word => (word, 1)).collect())
    logger.info("wordCount: {}", wordCount.collect())
    
  }
}