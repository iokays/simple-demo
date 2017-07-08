package com.iokays.spark.guide

import scala.annotation.varargs
import scala.beans.BeanInfo

import org.apache.spark.annotation.Since
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.Matrices
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix

object DataTypesIT {
  def main(args: Array[String]): Unit = {

    val dv: Vector = Vectors.dense(1.0, 0.0, 3.0)
    println(dv)
    val sv1: Vector = Vectors.sparse(3, Array(0, 2), Array(1.0, 3.0))
    println(sv1)
    val sv2: Vector = Vectors.sparse(3, Seq((0, 1.0), (2, 3.0)))
    println(sv2)
    
    val post = LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0))
    println(post)

    val neg = LabeledPoint(0.0, Vectors.sparse(3, Array(0, 2), Array(1.0, 3.0)))
    println(neg)

        val spark = SparkSession.builder().appName("DataTypesIT").master("local[4]").getOrCreate()
        val sc = spark.sparkContext
        
    //    val examples = MLUtils.loadLibSVMFile(sc, "data/mllib/sample_libsvm_data.txt")
    //    println(examples)
    //    
    val dm: Matrix = Matrices.dense(3, 2, Array(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
    println(dm)

    val sm: Matrix = Matrices.sparse(3, 2, Array(0, 1, 3), Array(0, 2, 1), Array(9, 6, 8))
    println(sm)
    
    val rows: RDD[org.apache.spark.mllib.linalg.Vector] = sc.makeRDD(Seq(org.apache.spark.mllib.linalg.Vectors.dense(1.0, 0.0, 3.0), org.apache.spark.mllib.linalg.Vectors.dense(1.0, 0.0, 3.0)))
      
    val mat: RowMatrix = new RowMatrix(rows)
    
    val r = mat.numRows()
    println(r)
    val c = mat.numCols();
    println(c)

    mat.tallSkinnyQR(true)
    
    val imat: IndexedRowMatrix = new IndexedRowMatrix(null)
    val r1 = imat.numRows()
    val c1 = imat.numCols()
    
    val rowRat: RowMatrix = imat.toRowMatrix()
    
    val cmat = new CoordinateMatrix(null);
    
    val r2 = cmat.numRows()
    val c2 = cmat.numCols()
    
    cmat.toIndexedRowMatrix()
    
    val bmat = cmat.toBlockMatrix().cache()
    
    bmat.validate()
    
    bmat.transpose.multiply(bmat)
  }
}