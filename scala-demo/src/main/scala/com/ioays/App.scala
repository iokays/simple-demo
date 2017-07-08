package com.ioays

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.DriverManager

object App {
    def main(args: Array[String]): Unit = {
        print("hello hello");
        lift3(DriverManager.getConnection)(Option("127.0.0.1"), Option("sulliy"), Option("sulliy"))
    }

    def lift3[A, B, C, D](f: Function3[A, B, C, D]): Function3[Option[A], Option[B], Option[C], Option[D]] = {
        (oa: Option[A], ob: Option[B], oc: Option[C]) =>
            for (a <- oa; b <- ob; c <- oc) yield f(a, b, c)
    }
}

