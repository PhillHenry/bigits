package uk.co.odinconsultants.bigits
import java.io.InputStream
import fs2.{Fallible, Stream}
import cats.effect.IO
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*

object StreamingCSV {
  def toT[T](is: InputStream)(implicit e: CsvRowDecoder[T, String]) = fs2.io
    .readInputStream(IO(is), 128)
    .covary[IO]
    .through(fs2.text.utf8.decode)
    .through(decodeUsingHeaders[T]())

}
