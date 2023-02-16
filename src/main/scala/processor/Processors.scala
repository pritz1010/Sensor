package processor

import java.io.File
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import cats.implicits._
import implicits._
import cats.effect._
import model.OutputData
import cats.effect.IO
import scala.collection.parallel.CollectionConverters._

import scala.concurrent.{ExecutionContextExecutor, Future}

/** Processing CSV-files in parallel using akka-streams. */
class ParallelAkkaProcessor(implicit cs: ContextShift[IO]) extends CsvProcessor {

  def run(directoryName: String): IO[OutputData] = IO.fromFuture(IO {
    implicit val system: ActorSystem = ActorSystem("Sensor")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val parallelism = 8
    val source = Source(getInputFiles(directoryName))
    val future = source
      .via(Flow[File].mapAsync(parallelism)(file => Future(processCsvFile(file))))
      .runWith(Sink.fold(IO(OutputData()))(_ |+| _))

    future.onComplete(_ => system.terminate())
    future
  }).flatMap(identity)
}


/** Processing CSV-files in parallel using parallel collections. */
class ParallelProcessor extends CsvProcessor {

  def run(directoryName: String): IO[OutputData] = getInputFiles(directoryName).par
    .map(file => processCsvFile(file))
    .fold(IO(OutputData()))(_ |+| _)
}


/** Sequential processing of CSV files. */
class SequentialProcessor extends CsvProcessor {
  def run(directoryName: String): IO[OutputData] =
    getInputFiles(directoryName).foldLeft(IO(OutputData())) { (either, file) => either.flatMap(processCsvFile(file, _)) }
}

/** Processing CSV-files sequentially using akka-streams. */
class SequentialAkkaProcessor(implicit cs: ContextShift[IO]) extends CsvProcessor {

  def run(directoryName: String): IO[OutputData] = IO.fromFuture(IO {
    implicit val system: ActorSystem = ActorSystem("Sensor")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val source = Source(getInputFiles(directoryName))
    val future = source
      .via(Flow[File].map(file => processCsvFile(file)))
      .runWith(Sink.fold(IO(OutputData()))(_ |+| _))

    future.onComplete(_ => system.terminate())
    future
  }).flatMap(identity)
}