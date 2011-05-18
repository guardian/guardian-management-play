package com.gu.management

import net.liftweb.http.{Req, XmlResponse}

object Status {
  def apply(metrics: Seq[TimingMetric]) = new Status(metrics)
}

class Status(metrics: Seq[TimingMetric]) extends ManagementPage {
  val managementSubPath = "status" :: Nil


  def render(r: Req) = XmlResponse(
    <status>
      <timings>
        {metrics map {_.toXml}}
      </timings>
    </status>)

}