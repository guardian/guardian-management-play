package com.gu.management

import java.util.Date


trait GangliaMetric {
  def asJson: StatusMetricJson
}

trait GangliaTimingMetric extends GangliaMetric {
  this: TimingMetric =>

  def name: String
  def title: String
  def description: String

  def asJson = StatusMetricJson(
    name = name,
    `type` = "timer",
    title = title,
    description = description,
    count = Some(count.toString),
    totalTime = Some(totalTimeInMillis.toString)
  )
}


case class StatusMetricJson(
  // this should always be set to either "application" or "jvm"
  group: String = "application",
  name: String,
  `type`: String,
  // a short (<40 chars) title for this metric
  title: String,
  // an as-long-as-you-like description of what this metric means
  // (used, e.g. on mouse over)
  description: String,
  // NB: these are deliberately strings - some json parsers have issues
  // with big numbers, see https://dev.twitter.com/docs/twitter-ids-json-and-snowflake
  value: Option[String] = None,
  count: Option[String] = None,
  totalTime: Option[String] = None,
  units: Option[String] = None)

case class StatusResponseJson(
 application: String = "mac",
 time: Long = new Date().getTime,
 metrics: List[StatusMetricJson] = Nil
)

