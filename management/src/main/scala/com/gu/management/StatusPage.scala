package com.gu.management

object StatusPage {
  def apply(application: String, metrics: Seq[Metric]) = new StatusPage(application, metrics)
}

class StatusPage(application: String, metrics: Seq[Metric]) extends JsonManagementPage {
  val path = "/management/status"
  def jsonObj = StatusResponseJson(application = application, metrics = metrics.map(_.asJson))
}