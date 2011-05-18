package com.gu.management

import java.util.concurrent.atomic.AtomicLong

object TimingMetric {
  val Empty = new TimingMetric("Empty")
}

class TimingMetric(statusElementName: String) {
  private val _totalTimeInMillis = new AtomicLong()
  private val _count = new AtomicLong()

  def recordTimeSpent(durationInMillis: Long) {
    _totalTimeInMillis.addAndGet(durationInMillis)
    _count.incrementAndGet
  }

  private def genericXml = <prefix>
    <count>{_count.get}</count>
    <totalTimeInMillis>{_totalTimeInMillis.get}</totalTimeInMillis>
  </prefix>

  def toXml = genericXml.copy(label = statusElementName)

  def totalTimeInMillis = _totalTimeInMillis.get
  def count = _count.get

  // to use this class, you can write your own wrappers
  // and call recordTimeSpent, or you may use this one
  // if you want.
  def apply[T](block: => T) {
    val s = new StopWatch
    val result = block
    recordTimeSpent(s.elapsed)
  }
}

