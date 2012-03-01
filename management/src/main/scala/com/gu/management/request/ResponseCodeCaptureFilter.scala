package com.gu.management.request

import javax.servlet.FilterChain
import javax.servlet.http.{ HttpServletResponseWrapper, HttpServletResponse, HttpServletRequest }
import com.gu.management.{ Loggable, CountMetric, AbstractHttpFilter }

object StatusCodeChecker {

  def serverError(statusCode: Int): Boolean =
    if (statusCode >= 500 && statusCode < 600) true else false

  def clientError(statusCode: Int): Boolean =
    if (statusCode >= 400 && statusCode < 500) true else false

}

class ServerErrorResponseCaptureFilter extends ResponseCodeCaptureFilter(ServerErrorCounter, StatusCodeChecker.serverError)

class ClientErrorResponseCaptureFilter extends ResponseCodeCaptureFilter(ClientErrorCounter, StatusCodeChecker.clientError)

class ResponseCodeCaptureFilter(metric: CountMetric, shouldCount: Int => Boolean, shouldLog: Boolean = true) extends AbstractHttpFilter with Loggable {

  def doHttpFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {

    val wrappedResponse = new HttpServletResponseWrapper(response) {
      var statusCode: Option[Int] = None

      override def setStatus(status: Int): Unit = {
        super.setStatus(status)
        statusCode = Some(status)
      }

      override def setStatus(status: Int, message: String): Unit = {
        super.setStatus(status, message)
        statusCode = Some(status)
      }

      override def sendError(status: Int, message: String): Unit = {
        super.sendError(status, message)
        statusCode = Some(status)
      }

    }

    chain.doFilter(request, wrappedResponse)

    wrappedResponse.statusCode match {
      case Some(statusCode) if (shouldCount(statusCode)) => metric.recordCount(1)
      case None if (shouldLog) => logger.warn("No status code set by application, unable to determine if metric should be updated")
      case _ =>
    }

  }

}