package com.gu.management.servlet

import com.gu.management._
import javax.servlet.FilterChain
import javax.servlet.http.{ HttpServletResponseWrapper, HttpServletResponse, HttpServletRequest }

object StatusCodeChecker {
  def serverError(statusCode: Int): Boolean = statusCode >= 500 && statusCode < 600

  def clientError(statusCode: Int): Boolean = statusCode >= 400 && statusCode < 500
}

class ServerErrorResponseCaptureFilter extends ResponseCodeCaptureFilter(ServerErrorCounter, StatusCodeChecker.serverError)
class ClientErrorResponseCaptureFilter extends ResponseCodeCaptureFilter(ClientErrorCounter, StatusCodeChecker.clientError)

class ResponseCodeCaptureFilter(metric: CountMetric, shouldCount: Int => Boolean) extends AbstractHttpFilter with Loggable {

  def doHttpFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
    val wrappedResponse = new HttpServletResponseWrapper(response) {
      var statusCode: Int = 200

      override def setStatus(status: Int) {
        super.setStatus(status)
        statusCode = status
      }

      override def setStatus(status: Int, message: String) {
        super.setStatus(status, message)
        statusCode = status
      }

      override def sendError(status: Int, message: String) {
        super.sendError(status, message)
        statusCode = status
      }

    }

    chain.doFilter(request, wrappedResponse)

    if (shouldCount(statusCode)) metric.recordCount(1)
  }
}
