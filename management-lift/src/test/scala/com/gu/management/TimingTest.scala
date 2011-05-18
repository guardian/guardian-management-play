package com.gu.management

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.slf4j.LoggerFactory

class TimingTest extends FlatSpec with ShouldMatchers {
  val log = LoggerFactory.getLogger(getClass)

  "timing" should "make it nice and simple to log timing info" in {
    Timing.info(log, "hello") {
      Thread sleep 500
    }
  }
}