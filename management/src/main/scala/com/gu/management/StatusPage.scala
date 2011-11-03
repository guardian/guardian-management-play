package com.gu.management

import javax.servlet.http.HttpServletRequest

object StatusPage {
  def apply(metrics: Seq[Metric]) = new StatusPage(metrics)
}

class StatusPage(metrics: Seq[Metric]) extends ManagementPage {
  val path = "/management/status"

  def get(req: HttpServletRequest) = XmlResponse(
    <status>
      {metrics map {_.toXml}}
    </status>)
}

class JsonStatusPage(metrics: List[GangliaMetric]) extends JsonManagementPage {
  val path = "/management/status.json"
  def jsonObj = StatusResponseJson(metrics = metrics.map(_.asJson))
}