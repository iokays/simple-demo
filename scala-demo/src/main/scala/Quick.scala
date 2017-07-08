import java.io.IOException

import scala.Vector
import scala.beans.BeanProperty
import scala.math.BigInt.int2bigInt

import org.junit.Test

import javax.inject.Inject
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull


object Quick {

  def main(args: Array[String]): Unit = {

  }
  
  def makeFriendWith(s: Student, f: Friend[Student]) {f.befriend(s) }
  
  def makeURL(fileName: String): String = { "hello world" }
  def getMiddle[T](a: Array[T]) = a(a.length / 2)
  
  def makePair[T: Manifest](first: T, second: T) {
    var r = new Array[T](2); r(0) = first; r(1) = second
  }
  
  def makeFriends(p: Pair[Person]) {}
 
  def firstLast[A, C](it: C)(implicit ev: C <:< Iterable[A]) = (it.head, it.tail)
  
  import scala.util.control.TailCalls._
  def evenLength(xs: Seq[Int]): TailRec[Boolean] =
    if (xs.isEmpty) done(true) else tailcall(oddLength(xs.tail))

  def oddLength(xs: Seq[Int]): TailRec[Boolean] =
    if (xs.isEmpty) done(true) else tailcall(evenLength(xs.tail))

  def ulcase(s: String) = Vector(s.toUpperCase(), s.toLowerCase())

  @Test(timeout = 0, expected = classOf[org.junit.Test.None])
  def testSomeFeature() {}

  @native def win32RegKeys(root: String, path: String): Array[String]

  def check(@NotNull password: String) {}

  def sum2(lst: List[Int]): Int = lst match {
    case Nil => 0
    case h :: t => h + sum2(t)
  }

  def sum(lst: List[Int]): Int =
    if (lst == Nil) 0 else lst.head + sum(lst.tail)

  def digits(n: Int): Set[Int] =
    if (n < 0) digits(-n)
    else if (n < 10) Set(n)
    else (digits(n / 10)) + n % 10

  def runInThread(block: => Unit) {
    new Thread {
      override def run() { block }
    }.start
  }
  def until(condition: => Boolean)(block: => Unit) {
    if (!condition) {
      block
      until(condition)(block)
    }
  }
  def indexOf(str: String, ch: Char): Int = {
    var i = 0
    until(i == str.length) {
      if (str(i) == ch) return i
      else i += 1
    }
    return -1
  }
  def mulBy(x: Int, y: Int) = x * y
  def mulOneAtTime(x: Int)(y: Int) = x * y

  def $(o: Any): Unit = {
    println("--------------------------------------------")
    println("class.name  : " + o.getClass().getName)
    println("object.value: " + o)
    println("--------------------------------------------")
  }
}

object IsCompound {
  def unapply(input: String) = input.contains(" ")
}

object Name {
  def unapply(input: String) = {
    val pos = input.indexOf(" ")
    if (pos == -1) None
    else Some(input.substring(0, pos), input.substring(pos + 1))
  }

  def unapplySeq(input: String): Option[Seq[String]] = {
    if (input.trim == "") None else Some(input.trim.split("\\s"))
  }

}

trait Logged {
  def log(msg: String) {}
}

trait LoggedException extends Exception with Logged {
  this: { def getMessage(): String } =>
  def log() { log(getMessage) }
}

class UnhappyException extends IOException with LoggedException {
  override def getMessage() = "arggh!"
}

trait Logger {
  def log(msg: String)
  def info(msg: String) { log("INFO: " + msg) }
  def warn(msg: String) { log("WARN: " + msg) }
  def severe(msg: String) { log("SEVERE: " + msg) }
}

trait TimestampLogger extends Logger {
  abstract override def log(msg: String) {
    super.log(new java.util.Date() + " " + msg)
  }
}

trait ShortLogger extends Logger {
  val maxLength: Int
  abstract override def log(msg: String) {
    super.log(if (msg.length <= maxLength) msg else msg.substring(0, maxLength - 3) + "...")
  }
}

trait ConsoleLogger extends Logger {
  override def log(msg: String) { println(msg) }
}

class Account(var balance: Double = 0.0) {}

class SavingsAccount extends Account with ConsoleLogger with ShortLogger {
  var interest = 0.0
  val maxLength = 15
  def withdraw(amount: Double) {
    if (amount > balance) log("Insufficient funds")
    else balance - amount
  }

  override def log(msg: String) { println(msg) }
}

abstract class Person extends Serializable {
  val id: Int
  var name: String
}

@SerialVersionUID(1L)
class Employee(val id: Int) extends Person with Serializable {
  var name = ""
}

class Creature {
  val range: Int = 10
  val env: Array[Int] = new Array[Int](range)
}

class Ant extends { override val range = 2 } with Creature {

}

class Fraction(var n: Int, var d: Int) {

}

object Fraction {
  def apply(n: Int, d: Int) = new Fraction(n, d)
  def unapply(input: Fraction) = if (input.d == 0) None else Some((input.n, input.d))
}

object Number {
  def unapply(input: String): Option[Int] = {
    try {
      Some(Integer.parseInt(input))
    } catch {
      case ex: NumberFormatException => None
    }
  }
}

sealed abstract class Amount

case class Dollar(value: Double) extends Amount
case class Currency(value: Double, unit: String) extends Amount
case object Nothing extends Amount

abstract class Item
case class Article(description: String, price: Double) extends Item
case class Bundle(description: String, price: Double, items: Item*) extends Item

sealed abstract class TrafficLightColor
case object Red extends TrafficLightColor
case object Yellow extends TrafficLightColor
case object Green extends TrafficLightColor

@Entity class Credentials[@specialized T] @Inject() {
  @Id @BeanProperty @NotNull var username: String = _
  @BeanProperty var password: String = _
}

class Book {
  @throws(classOf[IOException]) def read(filename: String) {}
}

object Util {
  def sum(xs: Seq[Int]): BigInt = {
    if (xs.isEmpty) 0 else xs.head + sum(xs.tail)
  }

  def sum2(xs: Seq[Int], partial: BigInt): BigInt = {
    if (xs.isEmpty) partial else sum2(xs.tail, xs.head + partial)
  }
}

class Pair[+T](val first: T, val second: T) {

}

trait Friend[-T] {
  def befriend(someone: T)
}





  

