package com.iokays.spark.action

import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType
import org.slf4j.LoggerFactory

object SparkSQLIT2 {

  val logger = LoggerFactory.getLogger("SparkSQLIT2")

  case class Person(name: String, age: Long)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[2]")
      .appName("Spark SQL basic IT")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
    runProgrammaticSchemaIT(spark)
  }

  private def runBasicDataFrameIT(spark: SparkSession): Unit = {
    val df = spark.read.json("src/main/resources/people.json")
    df.show()
    df.printSchema()
    df.select("name").show()
    df.select(functions.col("name"), functions.col("age")).show()
    df.select(functions.col("name"), functions.col("age").plus(1)).show()
    df.filter(functions.col("age").gt(21)).show()
    df.groupBy("age").count().show()

    df.createOrReplaceTempView("people")

    val sqlDF = spark.sql("select * from people")
    sqlDF.show()
  }

  private def runDatasetCreationIT(spark: SparkSession): Unit = {
    import spark.implicits._
    val caseClassDS = Seq(Person("Andy", 32)).toDF()
    caseClassDS.show()

    val primitiveDS = Seq(1, 2, 3).toDS()
    primitiveDS.map(_ + 1).collect()

    val path = "src/main/resources/people.json"
    val peopleDS = spark.read.json(path).as[Person]
    peopleDS.show()
  }

  private def runInferSchemaIT(spark: SparkSession): Unit = {
    import spark.implicits._

    val peopleDF = spark.sparkContext.textFile("src/main/resources/people.txt").map(_.split(",")).map(attributes => Person(attributes(0), attributes(1).trim.toInt)).toDF()

    peopleDF.createTempView("people")
    val teenagersDF = spark.sql("select name, age from people where age between 13 and 19")
    teenagersDF.map(teenager => "Name: " + teenager(0)).show()
    teenagersDF.map(teenager => "Name: " + teenager.getAs[String]("name")).show()

    implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
    teenagersDF.map(teenager => teenager.getValuesMap[Any](List("name", "age"))).collect()

  }

  private def runProgrammaticSchemaIT(spark: SparkSession): Unit = {
    import spark.implicits._

    val peopleRDD = spark.sparkContext.textFile("src/main/resources/people.txt")
    val schemaString = "name age"
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)
    val rowRDD = peopleRDD.map(_.split(" ")).map(attributes => Row(attributes(0), attributes(1).trim()))
    val peopleDF = spark.createDataFrame(rowRDD, schema)
    peopleDF.createOrReplaceTempView("people")

    val results = spark.sql("select * from people")
    results.map(attributes => "Name: " + attributes(0)).show()
  }

}

