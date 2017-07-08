package com.iokays.spark.action

import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream.toPairDStreamFunctions
import org.slf4j.LoggerFactory

object NetworkWordCountIT {
  val logger = LoggerFactory.getLogger("SparkStreamingIT")
  
  
  /**
   * nc -lk 9999
   */
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCountIT")
    val ssc = new StreamingContext(conf, Seconds(1))
    new NetworkWordCountIT(ssc, "192.168.0.230", 9999).start()
  }
}

class NetworkWordCountIT(private val ssc: StreamingContext, private val hostname: String, private val port: Int) {
  def start(): Unit = {
    val lines = ssc.socketTextStream(hostname, port)
    val words = lines.flatMap { _.split(" ") }
    val pairs = words.map { word => (word, 1) }
    val wordCounts = pairs.reduceByKey(_ + _)
    wordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  }
}