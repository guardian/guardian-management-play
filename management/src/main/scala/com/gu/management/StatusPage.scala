package com.gu.management

import javax.servlet.http.HttpServletRequest

object StatusPage {
  def apply(metrics: Seq[TimingMetric]) = new StatusPage(metrics)
}

class StatusPage(metrics: Seq[TimingMetric]) extends ManagementPage {
  val path = "/management/status"

  def get(req: HttpServletRequest) = XmlResponse(
    <status>
      <timings>
        {metrics map {_.toXml}}
      </timings>
    </status>)
}