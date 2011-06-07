package com.gu.management.request

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.gu.management.{StopWatch, TimingMetric, AbstractHttpFilter}
import collection.JavaConverters._
import java.net.URLEncoder
import org.slf4j.LoggerFactory

/*
 * If you want to change the behaviour of this class, derive a version
 * and call super with the parameters you want to set
 */
class RequestLoggingFilter(
    metric: TimingMetric = TimingMetric.empty,
    shouldLogParametersOnNonGetRequests: Boolean = false,
    parametersToSuppressInLogs: Set[String] = Set.empty,
    pathPrefixesToLogAtTrace: Set[String] = Set("/management"),
    maximumSizeForPostParameters: Int = 32
    ) extends AbstractHttpFilter {

  protected lazy val logger = LoggerFactory.getLogger(getClass)



  class Request(r: HttpServletRequest) {
    lazy val servletPath = Option(r.getServletPath) getOrElse ""
    lazy val pathInfo = Option(r.getPathInfo) getOrElse ""
    lazy val fullPath = servletPath + pathInfo

    lazy val method = r.getMethod

    lazy val paramNames = r.getParameterNames.asScala.map(_.toString).toList

    lazy val params = paramNames map { name => name -> Option(r.getParameter(name)).getOrElse("") }

    lazy val loggableParams = {
      if (shouldLogParametersOnNonGetRequests || method == "GET") {
        params map filterParamForLogging
      } else {
        Nil
      }
    }

    lazy val loggableParamString = loggableParams match {
      case Nil => ""
      case l => l.map { case (k, v) => k + "=" + URLEncoder.encode(v, "UTF-8") } mkString("?", "&", "")
    }

    def filterParamForLogging(p: Tuple2[String, String] ) = p match {
      case (k, v) if (parametersToSuppressInLogs contains k) =>
        k -> "*****"
      case (k, v) if ("POST" == method && v.length > maximumSizeForPostParameters) =>
        k -> (v.take(maximumSizeForPostParameters) + "...")
      case other =>
        other
    }

    lazy val shouldLog = ! (pathPrefixesToLogAtTrace exists { fullPath.startsWith })
  }

  def doHttpFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
    val req = new Request(request)

    val activity = req.method + " " + req.fullPath + req.loggableParamString

    val stopWatch = new StopWatch
    try {
      val (key, value) = AppServerHeader()
      response.setHeader(key, value)

      chain.doFilter(request, response)

      if (req.shouldLog) {
        logger.info(activity + " completed in " + stopWatch.elapsed + " ms")
        metric.recordTimeSpent(stopWatch.elapsed)
      }
    } catch {
      case t: Throwable =>
        logger.warn(activity + " caused exception after " + stopWatch.elapsed + " ms", t)
        throw t
    }
  }

}

