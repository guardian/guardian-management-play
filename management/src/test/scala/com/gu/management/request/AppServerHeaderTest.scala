package com.gu.management.request

import org.specs2.mutable.Specification

class AppServerHeaderTest extends Specification {
  "app server header calculator" should {
    "return hostname and thread in a string" in {

      // there's no point in mirroring the code here
      // so please test via visual inspection ;)
      val (key, value) = AppServerHeader()
      println("header is '" + value + "'")
      key must_== "X-GU-jas"
    }
  }

}