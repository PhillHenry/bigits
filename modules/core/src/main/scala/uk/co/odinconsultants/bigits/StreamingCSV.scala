package uk.co.odinconsultants.bigits
import java.io.InputStream
import fs2.Stream
import cats.effect.IO
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*

object StreamingCSV {
  def toT[T](is: InputStream)(implicit e: CsvRowDecoder[T, String]): Stream[IO, T] = fs2.io
    .readInputStream(IO(is), 128)
    .through(fs2.text.utf8.decode)
    .through(fs2.text.lines)
    .through(decodeUsingHeaders[T]())
}
