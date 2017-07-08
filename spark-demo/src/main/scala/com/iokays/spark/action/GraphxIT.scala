package com.iokays.spark.action

import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx.Graph.graphToGraphOps
import org.apache.spark.graphx.VertexId
import org.apache.spark.graphx.VertexRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

object GraphxIT {

  val logger = LoggerFactory.getLogger("GraphxIT")

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName(s"${this.getClass.getSimpleName}").getOrCreate()
    val sc = spark.sparkContext

    val users: RDD[(VertexId, (String, String))] = sc.parallelize(Array((3L, ("rxin", "student")), (7L, ("jgonzal", "postdoc")), (5L, ("franklin", "prof")), (2L, ("istoica", "prof")), (4L, ("peter", "student"))))

    val relationships: RDD[Edge[String]] = sc.parallelize(Array(Edge(3L, 7L, "collab"), Edge(5L, 3L, "advisor"), Edge(2L, 5L, "colleague"), Edge(5L, 7L, "pi"), Edge(4L, 0L, "student"), Edge(5L, 0L, "colleague")))

    val defaultUser = ("John Doe", "Missing")

    val graph = Graph(users, relationships, defaultUser)

    val count1 = graph.vertices.filter { case (id, (name, pos)) => pos == "postdoc" }.count
    println(count1)

    val count2 = graph.edges.filter(e => e.srcId > e.dstId).count
    println(count2)
    
    val count3 = graph.edges.filter {case Edge(src, dst, prop) => src > dst }.count
    println(count3)
    
    val facts: RDD[String] = 
      graph.triplets.map(triplet =>
        triplet.srcAttr._1 + "is the " + triplet.attr + " of " + triplet.dstAttr._1)
        
    facts.collect.foreach(println(_))
        
    
    val inDegress: VertexRDD[Int] = graph.inDegrees
    
//    val newVertices = graph.vertices.map {case (id, attr) => (id, mapUdf(id, attr))}
//    
//    val newGraph = Graph(newVertices, graph.edges)
//    val newGraph2 = graph.mapVertices((id, attr) => mapUdf(id, attr))
    
    val validGraph = graph.subgraph(vpred = (id, attr) => attr._2 != "Missing")
    
    validGraph.vertices.collect.foreach(println(_))
    
    validGraph.triplets.map(triplet => triplet.srcAttr._1 + " is the " + triplet.attr + " of " + triplet.dstAttr._1).collect.foreach(println(_))
    
    val ccGraph = graph.connectedComponents()
    val validGraph2 = graph.subgraph(vpred = (id, attr) => attr._2 != "Missging")
    val validCCGraph = ccGraph.mask(validGraph2)
    
    
  }
}