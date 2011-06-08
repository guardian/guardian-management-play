package com.gu.management

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.Callable

object TimingMetric {
  val empty = new TimingMetric("Empty")
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

