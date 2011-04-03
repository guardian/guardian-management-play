package com.gu.management.scalatra

import com.gu.management.timing.TimingMetric
import org.slf4j.LoggerFactory
import org.apache.commons.lang.time.StopWatch

trait Logging {

  private val log = LoggerFactory getLogger getClass

  def trace(msg: => String) = log.trace(msg)
  def trace(msg: => String, e: Throwable) = log.trace(msg, e)

  def debug(msg: => String) = log.debug(msg)
  def debug(msg: => String, e: Throwable) = log.debug(msg, e)

  def info(msg: => String) = log.info(msg)
  def info(msg: => String, e: Throwable) = log.info(msg, e)

  def warn(msg: => String) = log.warn(msg)
  def warn(msg: => String, e: Throwable) = log.warn(msg, e)

  def error(e: Throwable) = log.error(e.toString, e)
  def error(msg: => String) = log.error(msg)
  def error(msg: => String, e: Throwable) = log.error(msg, e)
}


// TODO: Use something in management-core instead?
trait LoggingStopWatch extends Logging {

  private lazy val stubMetric = new TimingMetric

  def time[X](activity: Any, metric: TimingMetric = stubMetric)(block: => X): X = {
    val stopWatch = new StopWatch
    stopWatch.start

    try {
      val x = block
      stopWatch.stop
      val time = stopWatch.getTime
      info("%s completed in %s ms".format(activity, time))
      metric recordTimeSpent time
      x
    } catch {
      case e: Exception =>
        stopWatch.stop
        warn("%s caused exception after %s ms".format(activity, stopWatch.getTime), e)
        throw e
    }
  }

  def verboseTime[X](activity: Any, metric: TimingMetric = stubMetric)(block: => X): X = {
    info("%s starting...".format(activity))
    time(activity, metric)(block)
  }

}