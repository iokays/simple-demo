package com.iokays.spark.action

import org.apache.spark.sql.SparkSession

object StructuredNetworkWordCountIT {
  def main(args: Array[String]): Unit = {
    val host = "192.168.0.230"
    val port = 9999

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCountIT")
      .master("local")
      .getOrCreate()

    import spark.implicits._

    // Create DataFrame representing the stream of input lines from connection to host:port
    val lines = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port)
      .load().as[String]

    // Split the lines into words
    val words = lines.flatMap(_.split(" "))

    // Generate running word count
    val wordCounts = words.groupBy("value").count()

    // Start running the query that prints the running counts to the console
    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    query.awaitTermination()
  }
}