package com.iokays.spark.classification

import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * https://github.com/sryza/aas/blob/master/ch04-rdf/src/main/scala/com/cloudera/datascience/rdf/RunRDF.scala
 */
object RunRDF {
    def main(args: Array[String]): Unit = {

        //SparkSession 本地环境.
        val spark = SparkSession.builder().master("local[4]").appName(s"${this.getClass.getName}").getOrCreate()
        import spark.implicits._

        //读取文件
        val dataWithoutHeader = spark.read.option("inferSchema", true).option("header", false)
            .csv("src/main/resources/com/iokays/data/covtype/covtype.data")

        val colNames = Seq("Elevation", "Aspect", "Slope",
            "Horizontal_Distance_To_Hydrology", "Vertical_Distance_To_Hydrology",
            "Horizontal_Distance_To_Roadways",
            "Hillshade_9am", "Hillshade_Noon", "Hillshade_3pm",
            "Horizontal_Distance_To_Fire_Points") ++ (0 until 4)
            .map(i => s"Wilderness_Area_$i") ++ (0 until 40).map(i => s"Soil_Type_$i") ++ Seq("Cover_Type")

        //设置列名, Cover_Type 强制转为double类型
        val data = dataWithoutHeader.toDF(colNames: _*).withColumn("Cover_Type", $"Cover_Type".cast("double"))
        
        val Array(trainData, testData) = data.randomSplit(Array(9.0, 1.0))    //训练集(90%),测试集(10%)
        trainData.cache
        testData.cache
        
        val runRDF = new RunRDF(spark)
        runRDF.simpleDecisionTree(trainData, testData)
    }
}

class RunRDF(private val spark: SparkSession) {
    def simpleDecisionTree(trainData: DataFrame, testData: DataFrame): Unit = {
        import spark.implicits._
        val inputCols = trainData.columns.filter(_ != "Cover_Type")
        
        //从源数据中提取特征指标数据
        val vectorAssembler = new VectorAssembler().setInputCols(inputCols).setOutputCol("featureVector")
        val assemblerTrainData = vectorAssembler.transform(trainData)
        assemblerTrainData.show(10, truncate = false)
        assemblerTrainData.select("featureVector").show(10, truncate = false)
        
    }
}






























