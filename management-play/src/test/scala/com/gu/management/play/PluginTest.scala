package com.gu.management.play

import org.specs2.mutable.Specification

trait TestTrait {
  def method: String
  val field: String
}
object TestCompanionObject extends TestTrait {
  def method: String = "method return value"
  val field: String = "field value"
}

class PluginTest extends Specification {

  "Companion reflector" should {
    "find companion object" in {
      val myObject = CompanionReflector.companion[TestTrait]("com.gu.management.play.TestCompanionObject")
      myObject.method must equalTo("method return value")
      myObject.field must equalTo("field value")
    }
  }

}
