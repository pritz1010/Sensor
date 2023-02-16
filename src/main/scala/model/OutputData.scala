package model

import cats.Semigroup
import cats.implicits._
import implicits._

import scala.collection.immutable.HashMap
import scala.util.Properties


case class OutputData(
                       sensors: Map[String, Aggregation] = HashMap().withDefaultValue(FailedAggregation),
                       failedCount: Int = 0,
                       totalCount: Int = 0,
                       fileCount: Int = 0
                     ) {

  def +(rhs: Measurement): OutputData = {
    OutputData(
      sensors + (rhs.id -> (sensors(rhs.id) + rhs.humidity)),
      failedCount + (if (rhs.humidity.isDefined) 0 else 1),
      totalCount + 1,
      fileCount
    )
  }

  def |+|(rhs: OutputData): OutputData = {
    OutputData(
      sensors |+| rhs.sensors,
      failedCount + rhs.failedCount,
      totalCount + rhs.totalCount,
      fileCount + rhs.fileCount
    )
  }

  /** String representation of [[model.OutputData]] */
  override def toString: String = {
    // Reversed ordering by average.
    implicit val ordering: Ordering[(String, Aggregation)] =
      (x: (String, Aggregation), y: (String, Aggregation)) => (x._2.average, y._2.average) match {
        case (None, None) => 0
        case (None, _) => 1
        case (_, None) => -1
        case (Some(a), Some(b)) => b compare a
      }

    s"""Num of processed files: $fileCount
       |Num of processed measurements: $totalCount
       |Num of failed measurements: $failedCount
       |
       |Sensors with highest avg humidity:
       |
       |sensor-id,min,avg,max
       |${
      sensors.toSeq.sorted.map { case (id, data) => s"$id,$data" }.mkString(Properties.lineSeparator)
    }""".stripMargin
  }
}

/** Semigroup for [[model.OutputData]] */
trait OutputDataSemigroupImpl extends Semigroup[OutputData] {
  def combine(lhs: OutputData, rhs: OutputData): OutputData = lhs |+| rhs
}

/** Provide implicit semigroup for [[model.OutputData]] */
trait OutputDataSemigroup {

  implicit object outputDataSemigroupImpl extends OutputDataSemigroupImpl

}