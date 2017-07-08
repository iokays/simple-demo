package com.iokays.spark.action

import scala.reflect.runtime.universe

import org.apache.spark.SparkContext
import org.apache.spark.annotation.Since
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.ml.recommendation.ALSModel
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.slf4j.LoggerFactory

object RunRecommender {
  val logger = LoggerFactory.getLogger("RunRecommender")
  
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("RunRecommender").master("local[4]")
    .config("spark.executor.memory", "6g")
    .config("spark.sql.warehouse.dir", "file:///D:/path/to/my/").getOrCreate();
    
    val rawUserArtistData = spark.read.textFile("hdfs://n1.devct.com:8020/tmp/pyb/user_artist_data.txt")
    
    val rawArtistData = spark.read.textFile("hdfs://n1.devct.com:8020/tmp/pyb/artist_data.txt")
    val rawArtistAlias = spark.read.textFile("hdfs://n1.devct.com:8020/tmp/pyb/artist_alias.txt")
    val runRecommender = new RunRecommender(spark)
    runRecommender.preparation(rawUserArtistData, rawArtistData, rawArtistAlias)
    runRecommender.model(rawUserArtistData, rawArtistData, rawArtistAlias)
  }
}

class RunRecommender(private val spark: SparkSession) {
  
  import spark.implicits._
  
  def preparation(
      rawUserArtistData: Dataset[String],
      rawArtistData: Dataset[String], rawArtistAlias: Dataset[String]): Unit = {
    
    val userArtistDF = rawUserArtistData.map { line =>
      val Array(user, artist, _*) = line.split(' ')
      (user.toInt, artist.toInt)
    }.toDF("user", "artist")
    
    userArtistDF.agg(min("user"), max("user"), min("artist"), max("artist")).show()
    
    val artistByID = buildArtistById(rawArtistData)
    val artistAlias = buildArtistAlias(rawArtistAlias)
    
    val(badID, goodID) = artistAlias.head
    artistByID.filter($"id" isin (badID, goodID)).show()
  }
  
  def model(
      rawUserArtistData: Dataset[String], 
      rawArtistData: Dataset[String],
      rawArtistAlias: Dataset[String]): Unit = {
    
    val bArtistAlias = spark.sparkContext.broadcast(buildArtistAlias(rawArtistAlias))
    
    val trainData = buildCounts(rawUserArtistData, bArtistAlias)
    
    val model = new ALS().setImplicitPrefs(true).
      setRank(10).
      setRegParam(0.01).
      setAlpha(40).
      setMaxIter(5).
      setUserCol("user").
      setItemCol("artist").
      setRatingCol("count").
      setPredictionCol("prediction").
      fit(trainData)
      
    trainData.unpersist()
    
    model.userFactors.select("features").show(truncate=true)
    
    val userID = 2093760
    
    val existingArtistIDs = trainData.filter($"user" === userID).select("artist").as[Int].collect()
    
    val artistByID = buildArtistById(rawArtistData)
    
    artistByID.filter($"id" isin (existingArtistIDs:_*)).show()
    
    val topRecommendations = makeRecommendations(model, userID, 5)
    topRecommendations.show()
    
    val recommendedArtistIDs = topRecommendations.select("artist").as[Int].collect()
    
    artistByID.filter($"id" isin (recommendedArtistIDs:_*)).show()
    
    model.userFactors.unpersist()
    model.itemFactors.unpersist()
    
  }
  
  def evaluate(
      rawUserArtistData: Dataset[String],
      rawArtistAlias: Dataset[String]): Unit = {
    val bArtistAlias = spark.sparkContext.broadcast(buildArtistAlias(rawArtistAlias))
    
    val allData = buildCounts(rawUserArtistData, bArtistAlias)
    val Array(trainData, cvData) = allData.randomSplit(Array(0.9, 0.1))
    trainData.cache()
    cvData.cache()
    
    val allArtistIDs = allData.select("artist").as[Int].distinct().collect()
    
  }
  
  def buildArtistById(rawArtistData: Dataset[String]): DataFrame = {
    rawArtistData.flatMap { line =>
      val (id, name) = line.span(_ != '\5')
      if (name.isEmpty()) {
        None
      } else {
        try {
          Some((id.toInt, name.trim))
        } catch {
          case _: NumberFormatException => None
        }
      }
    }.toDF("id", "name")
  }
  
  def buildArtistAlias(rawArtistAlias: Dataset[String]): Map[Int, Int] = {
    rawArtistAlias.flatMap { line => 
      val Array(artist, alias) = line.split('\t')
      if (artist.isEmpty) {
        None
      } else {
        Some((artist.toInt, alias.toInt))
      }
    }.collect().toMap
  }
  
  def buildCounts(
      rawUserArtistData: Dataset[String],
      bArtistAlias: Broadcast[Map[Int,Int]]): DataFrame = {
      rawUserArtistData.map { line =>
        val Array(userID, artistID, count) = line.split(' ').map(_.toInt)
        val finalArtistID = bArtistAlias.value.getOrElse(artistID, artistID)
        (userID, finalArtistID, count)
    }.toDF("user", "artist", "count")
  }
  
  def makeRecommendations(model: ALSModel, userID: Int, howMany: Int) : DataFrame = {
    val toRecommend = model.itemFactors.
    select($"id".as("artist")).
    withColumn("user", lit(userID))
    model.transform(toRecommend).
    select("artist", "prediction").
    orderBy($"prediction".desc).
    limit(howMany)
  }
  
}