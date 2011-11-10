package com.gu.management

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.Callable
import java.util.Date

abstract class Metric()  {
  def asJson: StatusMetric
}

object TimingMetric {
  val empty = new TimingMetric("Empty","Empty","Empty")
}

class CountMetric(name: String, title: String, description: String) extends Metric {
  private val _count = new AtomicLong()

  def recordCount(count: Int) {
     _count.addAndGet(count)
  }

  def count = _count.get

 def asJson = StatusMetric(
    name = name,
    `type` = "counter",
    title = title,
    description = description,
    count = Some(count.toString)
  )
}

class TimingMetric(name: String, title: String, description: String ) extends Metric() {
  def this(name: String) = this(name,name,name)

  private val _totalTimeInMillis = new AtomicLong()
  private val _count = new AtomicLong()

  def recordTimeSpent(durationInMillis: Long) {
    _totalTimeInMillis.addAndGet(durationInMillis)
    _count.incrementAndGet
  }

  def asJson = StatusMetric(
    name = name,
    `type` = "timer",
    title = title,
    description = description,
    count = Some(count.toString),
    totalTime = Some(totalTimeInMillis.toString)
  )

  def totalTimeInMillis = _totalTimeInMillis.get
  def count = _count.get

  // to use this class, you can write your own wrappers
  // and call recordTimeSpent, or you may use this one
  // if you want.
  // val t = TimingMetric("example")
  // ...
  // t measure {
  //   code here
  // }
  def measure[T](block: => T) = {
    val s = new StopWatch
    val result = block
    recordTimeSpent(s.elapsed)
    result
  }

  // for java developers, these are easier to call
  def call[T](c: Callable[T]) = measure { c.call }
  def run(r: Runnable) = measure { r.run() }
}

case class StatusMetric(
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
 application: String,
 time: Long = new Date().getTime,
 metrics: Seq[StatusMetric] = Nil
)




















