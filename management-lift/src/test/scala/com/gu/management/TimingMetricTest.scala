package com.gu.management

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec

class TimingMetricTest extends FlatSpec with ShouldMatchers {
  "timing metric" should "provide an easy way to time a block" in {
    val t = new TimingMetric("test")

    t {
      Thread.sleep(500)
    }

    t.count should be (1)
    t.totalTimeInMillis should be >= (400L)
  }
}