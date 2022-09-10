package uk.co.odinconsultants.bigits

import weaver.{FunSuite, SimpleIOSuite}
import weaver.scalacheck.Checkers
import cats.effect.IO
import cats.data.NonEmptyList
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*

import java.io.ByteArrayInputStream

object StreamingCSVSuite extends SimpleIOSuite with Checkers:

  case class Line(string: String, integer: Int)

  def makeLines(n: Int): String =
    "string,integer\n" + (1 to n).map((i: Int) => s"$i,$i").mkString("\n")

  implicit val myRowDecoder: CsvRowDecoder[Line, String] = deriveCsvRowDecoder

  test("CSV stream is parsed") {
    val n = 10
    for {
      count <-
        StreamingCSV.toT[Line](new ByteArrayInputStream(makeLines(n).getBytes())).compile.count
    } yield expect.same(count, n)
  }
