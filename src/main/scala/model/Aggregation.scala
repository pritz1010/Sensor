package model

import cats.Semigroup

/** Aggregated data from a sensor */
trait Aggregation {
  def +(rhs: Option[Int]): Aggregation

  def |+|(rhs: Aggregation): Aggregation

  def average: Option[Int]
}

/** Semigroup for [[model.Aggregation]] */
trait AggregationSemigroupImpl extends Semigroup[Aggregation] {
  def combine(lhs: Aggregation, rhs: Aggregation): Aggregation = lhs |+| rhs
}

/** Provide implicit semigroup for [[model.Aggregation]] */
trait AggregationSemigroup {
  implicit object aggregationSemigroupImpl extends AggregationSemigroupImpl

}