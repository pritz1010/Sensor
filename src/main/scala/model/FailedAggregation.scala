package model

case object FailedAggregation extends Aggregation {

  def +(rhs: Option[Int]): Aggregation = rhs match {
    case None => this
    case Some(v) => ValidAggregation(v, v, v, 1)
  }

  def |+|(rhs: Aggregation): Aggregation = rhs

  /** String representation of [[model.FailedAggregation]] */
  override def toString: String = "NaN,NaN,NaN"

  /** Average value */
  override def average: Option[Int] = None
}
