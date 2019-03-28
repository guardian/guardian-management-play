package com.gu.management.play

import play.api.libs.concurrent.Execution.Implicits._
import _root_.play.api.mvc._
import concurrent.Future
import util.Try
import com.gu.management._
import akka.stream.Materializer
import javax.inject.{Singleton, Inject}

object RequestMetrics {

  @Singleton class Standard @Inject() (mat: Materializer) {
    val knownResultTypeCounters = List(OkCounter(), RedirectCounter(), NotFoundCounter(), ErrorCounter())

    val otherCounter = OtherCounter(knownResultTypeCounters)

    val asFilters: List[MetricsFilter] = List(new TimingFilter(mat), new CountersFilter(otherCounter :: knownResultTypeCounters, mat))

    val asMetrics: List[Metric] = asFilters.flatMap(_.metrics).distinct
  }

  abstract class MetricsFilter extends Filter {
    val metrics: Seq[Metric]
  }

  class TimingFilter(override val mat: Materializer) extends MetricsFilter {
    val timingMetric = new TimingMetric("performance", "request_duration", "Client requests", "incoming requests to the application")

    val metrics = Seq(timingMetric)

    override def apply(next: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
      val s = new StopWatch
      val result = next(request)
      println(s"Hello Timing $request")
      result.onComplete { _ => timingMetric.recordTimeSpent(s.elapsed) }
      result
    }
  }
  
  class CountersFilter(counters: List[Counter], override val mat: Materializer) extends MetricsFilter {
    val metrics = counters.map(_.countMetric)
    
    override def apply(next: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
      val result = next(request)
      println(s"Hello Counters $request")
      result.onComplete(resultTry => counters.foreach(_.submit(resultTry)))
      result
    }
  }

  case class Counter(condition: Try[Result] => Boolean, countMetric: CountMetric) {
    def submit(resultTry: Try[Result]) {
      if (condition(resultTry)) countMetric increment ()
    }
  }

  object OkCounter {
    def apply() = Counter(StatusCode(200), new CountMetric("request-status", "200_ok", "200 Ok", "number of pages that responded 200"))
  }

  object RedirectCounter {
    def apply() = Counter(StatusCode(301, 302), new CountMetric("request-status", "30x_redirect", "30x Redirect", "number of pages that responded with a redirect"))
  }

  object NotFoundCounter {
    def apply() = Counter(StatusCode(404), new CountMetric("request-status", "404_not_found", "404 Not found", "number of pages that responded 404"))
  }

  object ErrorCounter {
    def apply() = Counter(t => { t.isFailure || StatusCode(500 to 509)(t) }, new CountMetric("request-status", "50x_error", "50x Error", "number of pages that responded 50x"))
  }

  object OtherCounter {
    def apply(knownResultTypeCounters: Seq[Counter]) = {
      def unknown(result: Try[Result]) = !knownResultTypeCounters.exists(_.condition(result))

      Counter(unknown, new CountMetric("request-status", "other", "Other", "number of pages that responded with an unexpected status code"))
    }
  }

  object StatusCode {

    def apply(codes: Traversable[Int]): Try[Result] => Boolean = apply(codes.toSet: Int => Boolean)

    def apply(codes: Int*): Try[Result] => Boolean = apply(Set(codes: _*): Int => Boolean)

    def apply(condition: Int => Boolean)(resultTry: Try[Result]) =
      resultTry.map(plainResult => condition(plainResult.header.status)).getOrElse(false)
  }

}
