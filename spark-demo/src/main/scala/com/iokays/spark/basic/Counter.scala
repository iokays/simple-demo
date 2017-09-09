package com.iokays.spark.basic

import org.slf4j.LoggerFactory
import org.apache.spark.sql.SparkSession

object Counter {

  val logger = LoggerFactory.getLogger("Counter")

  def main(args: Array[String]): Unit = {
    logger.debug("start")
    val sc = SparkSession.builder().appName("counter").master("local[4]").getOrCreate.sparkContext
    val textFile3000 = sc.textFile("D:/temp/file/3000.txt")
    val _3000o = textFile3000.flatMap( line => line.split(" ")).map( value => (value, 1)).reduceByKey((a, b) => a + b)
    val _3000ed = textFile3000.flatMap( line => line.split(" ")).map( value => (value + "ed", 1)).reduceByKey((a, b) => a + b)
    val _3000ing = textFile3000.flatMap( line => line.split(" ")).map( value => (value + "ing", 1)).reduceByKey((a, b) => a + b)
    val _3000 = _3000o.union(_3000ed).union(_3000ing)

    logger.debug("_3000.count: {}, map:{}", _3000.count(), _3000.collect())
    val snow = sc.textFile("D:/temp/file/the snowman and the snowdog.txt").flatMap( line => line.split(" ") ).map( value => (value, 1)).reduceByKey( (a, b) => a + b )
    logger.debug("snow.count: {}, map:{}", snow.count(), snow.collect())
    val hamlet = sc.textFile("D:/temp/file/William Shakespeare Hamlet.txt").flatMap( line => line.split(" ") ).map( value => (value, 1)).reduceByKey( (a, b) => a + b )
    logger.debug("hamlet.count: {}, map:{}", hamlet.count(), hamlet.collect())
  }
}