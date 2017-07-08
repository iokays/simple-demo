package com.ioays

object Timer {
  
  def oncePerSecond(callback: () => Unit) {
    while (true) {callback(); Thread sleep 1000}
  }
  
  def timeFiles() {
    println("time files like an arrow....")
  }
  
  def main(args: Array[String]): Unit = {
    val c = new Complex(1.2, 3.4)
    println(c.toString())
  }
}

class Complex(real: Double, imaginary: Double) {
  def re = real
  def im = imaginary
  
  override def toString() = "" + re + (if (im < 0) "" else "+") + im + "i"
}