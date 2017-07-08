package com.iokays.spark.action

import scala.util.Random

import org.apache.spark.annotation.Experimental
import org.apache.spark.annotation.Since
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession

/**
 * https://github.com/sryza/aas/blob/master/ch04-rdf/src/main/scala/com/cloudera/datascience/rdf/RunRDF.scala
 */
object RunRDF3 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[4]").appName(s"${this.getClass.getSimpleName}").getOrCreate();
    import spark.implicits._

    val dataWithoutHeader = spark.read.option("inferSchema", true).option("header", false)
      .csv("hdfs://n1.devct.com:8020/tmp/spark/data/covtype/covtype.data")

    val colNames = Seq("Elevation", "Aspect", "Slope",
      "Horizontal_Distance_To_Hydrology", "Vertical_Distance_To_Hydrology",
      "Horizontal_Distance_To_Roadways",
      "Hillshade_9am", "Hillshade_Noon", "Hillshade_3pm",
      "Horizontal_Distance_To_Fire_Points") ++ (0 until 4).map(i => s"Wilderness_Area_$i") ++ (0 until 40).map(i => s"Soil_Type_$i") ++ Seq("Cover_Type")

    val data = dataWithoutHeader.toDF(colNames: _*).withColumn("Cover_Type", $"Cover_Type".cast("double"))

    data.show()
    data.head

    val Array(trainData, testData) = data.randomSplit(Array(0.9, 0.1))
    trainData.cache
    testData.cache

    new RunRDF2(spark).simpleDecisionTree(trainData, testData)
  }
}
