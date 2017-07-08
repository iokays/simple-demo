package com.iokays.spark.guide

import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.MultivariateStatisticalSummary
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object DataTypesIT2 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("DataTypesIT2").master("local[4]").getOrCreate()
    val sc = spark.sparkContext
    val observations = sc.parallelize(Seq(
      Vectors.dense(1.0, 10.0, 100.0),
      Vectors.dense(2.0, 20.0, 200.0),
      Vectors.dense(3.0, 30.0, 300.0),
      Vectors.dense(4.0, 40.0, 400.0),
      Vectors.dense(5.0, 50.0, 500.0)))

    val summary: MultivariateStatisticalSummary = Statistics.colStats(observations)
    val mean = summary.mean
    println(s"mean: $mean")
    
    val variance = summary.variance
    println(s"variance: $variance")
    
    val numNonzeros = summary.numNonzeros
    println(s"numNonzeros: $numNonzeros")
    
    val seriesX: RDD[Double] = sc.parallelize(Array(1, 2, 3, 3, 5))
    val seriesY: RDD[Double] = sc.parallelize(Array(11, 22, 33, 33, 555))
    
    val correlation: Double = Statistics.corr(seriesX, seriesY, "pearson")
    println(s"Correlation is: $correlation")

    val correlation2: Double = Statistics.corr(seriesX, seriesY, "spearman")
    println(s"Correlation is: $correlation2")
    
    val data: RDD[Vector] = sc.parallelize(
        Seq(
            Vectors.dense(1.0, 10.0, 100.0),
            Vectors.dense(2.0, 20.0, 200.0),
            Vectors.dense(5.0, 33.0, 366.0)
            )
    )
    
    val correlMatrix: Matrix = Statistics.corr(data, "pearson")
    println(correlMatrix.toString())
    
    val data2 = sc.parallelize(Seq((1, 'a'), (1, 'b'), (2, 'c'), (2, 'd'), (2, 'e'), (3, 'f')))
    
    val fractions = Map(1 -> 0.1, 2-> 0.6, 0 -> 0.3)
    
//    val approxSample = data2.sampleByKey(withReplacement = false, fractions = fractions)
//   
//    val exactSample = data2.sampleByKeyExact(withReplacement = false, fractions = fractions)
    
    
    
  }
}