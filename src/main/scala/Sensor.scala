import cats.effect._
import cats.implicits._
import processor.CsvProcessor

object Sensor extends IOApp {
  /**
   * @param args command line arguments
   *             args(0) - Required input directory name
   *             args(1) - optional CsvProcessor type name
   * @return exit code
   */

  override def run(args: List[String]): IO[ExitCode] = {

    if (args.headOption.isEmpty) throw new IllegalArgumentException("Input directory not provided")
    val dirctoryPath = args.headOption.getOrElse(".")

    val ioExit = for {
      csvProcessor <- IO(CsvProcessor(args.get(1)))
      result <- csvProcessor.run(dirctoryPath)
      _ <- IO(println(result))
    } yield ExitCode.Success

    ioExit.handleError(throwable => {
      println(throwable)
      ExitCode.Error
    })
  }
}