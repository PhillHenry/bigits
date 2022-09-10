package uk.co.odinconsultants.bigits

import weaver.{FunSuite, SimpleIOSuite}
import weaver.scalacheck.Checkers
import cats.effect.IO
import cats.data.NonEmptyList
import uk.co.odinconsultants.mss.StreamingWordCount
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*

import java.io.ByteArrayInputStream

object StreamingCSVSuite extends SimpleIOSuite with Checkers:

  case class Line(string: String, integer: Int)

  def makeLines(n: Int): String = (1 to n).map((i: Int) => Line(i.toString, i)).mkString("\n")

  test("CSV stream is parsed") {
    implicit val myRowDecoder: CsvRowDecoder[Line, String] = deriveCsvRowDecoder
    for {
      count <-
        StreamingCSV.toT[Line](new ByteArrayInputStream(makeLines(100).getBytes())).compile.count
    } yield expect.same(count, 100)
  }
