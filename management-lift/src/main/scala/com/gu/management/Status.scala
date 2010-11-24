package com.gu.management

import net.liftweb.http.XmlResponse
import java.util.concurrent.atomic.AtomicLong

object TimingMetric {
  val Empty = new TimingMetric("Empty") 
}

class TimingMetric(statusElementName: String) {
  private val totalTimeInMillis = new AtomicLong()
  private val count = new AtomicLong()

  def recordTimeSpent(durationInMillis: Long) {
    totalTimeInMillis.addAndGet(durationInMillis)
    count.incrementAndGet
  }

  private def genericXml = <prefix>
    <count>{count.get}</count>
    <totalTimeInMillis>{totalTimeInMillis.get}</totalTimeInMillis>
  </prefix>

  def toXml = genericXml.copy(label = statusElementName)
}

object Status {
  def apply(metrics: Seq[TimingMetric]) = new Status(metrics)
}

class Status(metrics: Seq[TimingMetric]) extends ManagementPage {
  val managementSubPath = "status" :: Nil


  def response = XmlResponse(
    <status>
      <timings>
        {metrics map {_.toXml}}
      </timings>
    </status>)

}