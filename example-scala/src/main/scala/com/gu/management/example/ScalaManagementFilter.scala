package com.gu.management.example

import com.gu.management._

object DummyPage extends ManagementPage {
  val path = "/management/xxxx"
}

class ScalaManagementFilter extends ManagementFilter {
  lazy val pages = DummyPage :: Nil
}