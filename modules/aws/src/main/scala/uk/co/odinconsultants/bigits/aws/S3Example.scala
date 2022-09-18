package uk.co.odinconsultants.bigits.aws

import cats.implicits.*
import cats.effect.{ExitCode, IO, IOApp, Resource}
import fs2.aws.s3.S3
import io.laserdisc.pure.s3.tagless.S3AsyncClientOp
import io.laserdisc.pure.s3.tagless.{S3AsyncClientOp, Interpreter as S3Interpreter}
import eu.timepit.refined.types.string.NonEmptyString
import fs2.aws.s3.S3
import fs2.aws.s3.models.Models.{BucketName, FileKey}
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient

import java.net.URI

object S3Example extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val accessKey: String                = args(0)
    val secret: String                   = args(1)
    val bucketName: String               = args(2)
    val fileKey: String                  = args(3)
    val credentials: AwsBasicCredentials = AwsBasicCredentials.create(accessKey, secret)
    s3StreamResource(credentials).use(s3 =>
      S3.create(s3).flatMap(program(bucketName, fileKey)).as(ExitCode.Success)
    )
  }

  def s3StreamResource(credentials: AwsBasicCredentials): Resource[IO, S3AsyncClientOp[IO]] =
    S3Interpreter[IO].S3AsyncClientOpResource(
      S3AsyncClient
        .builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.EU_WEST_2)
    )

  def program(bucketName: String, fileKey: String)(s3: S3[IO]): IO[Unit] =
    s3.readFile(
      BucketName(NonEmptyString.unsafeFrom(bucketName)),
      FileKey(NonEmptyString.unsafeFrom(fileKey)),
    ).through(fs2.text.utf8.decode)
      .through(fs2.text.lines)
      .evalMap(line => IO(println(line)))
      .compile
      .drain
}
