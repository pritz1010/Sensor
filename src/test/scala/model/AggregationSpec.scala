package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class AggregationSpec extends AnyFlatSpec with should.Matchers {
  "A valid aggregation" should "be the same after combining with failed aggregation" in {
    val valid = ValidAggregation(2, 10, 12, 2)
    val combined = valid |+| FailedAggregation
    combined should be(valid)
  }

  it should "be valid aggregation after combining with valid aggregation" in {
    val valid0 = ValidAggregation(2, 10, 12, 2)
    val valid1 = ValidAggregation(1, 15, 20, 3)
    val combined = valid0 |+| valid1
    combined should be(ValidAggregation(1, 15, 32, 5))
  }

  "A failed aggregation" should "be equal to the aggregation, with which it combines" in {
    val valid = ValidAggregation(2, 10, 12, 2)
    val combined = FailedAggregation |+| valid
    combined should be(valid)
  }

  it should "be be failed after combining with failed aggregation" in {
    val combined = FailedAggregation |+| FailedAggregation
    combined should be(FailedAggregation)
  }
}
