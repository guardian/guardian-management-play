package com.gu.management

import org.slf4j.Logger


class StopWatch {
  val startTime = System.currentTimeMillis
  def elapsed = System.currentTimeMillis - startTime
}

object Timing {

  def debug[T](logger: Logger, activity: String)(block: => T): T =
    doIt(activity, block, logger.debug, logger.debug)

  def debug[T](activity: String)(block: => T)(implicit logger: Logger): T =
    debug(logger, activity)(block)

  def info[T](logger: Logger, activity: String)(block: => T): T =
    doIt(activity, block, logger.info, logger.info)

  def info[T](activity: String)(block: => T)(implicit logger: Logger): T =
    info(logger, activity)(block)


  private def doIt[T](activity: String, block: => T,
                      onSuccess: (String) => Unit, onFailure: (String, Throwable) => Unit): T = {
    val stopWatch = new StopWatch
    try {
      val result = block
      onSuccess(activity + " completed in " + stopWatch.elapsed + " ms")
      result
    } catch {
      case t: Throwable =>
        onFailure(activity + " caused exception after " + stopWatch.elapsed + " ms", t)
        throw t
    }
  }

}
