package com.iokays.spark.basic

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.feature.VectorAssembler

object VectorAssemblerIT {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[4]").appName("VectorAssembler").getOrCreate()
    
    val dataset = spark.createDataFrame(Seq((0, 18, 1.0, Vectors.dense(Array(0.0, 10, 0.5)), 10))).toDF("id", "hour",  "mobile", "userFeature", "clicked")
    
    val assemble = new VectorAssembler().setInputCols(Array("hour", "mobile", "userFeature")).setOutputCol("features")
    
    val output = assemble.transform(dataset)
    
    println(output.select("features", "clicked").first())
    
  }
}