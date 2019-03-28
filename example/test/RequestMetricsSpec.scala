import com.gu.management.{ Metric, TimingMetric, CountMetric }
import conf.PlayExampleRequestMetrics
import org.specs2.mutable._
import play.api.inject.guice.GuiceApplicationBuilder

import play.api.Mode
import play.api.test._
import play.api.test.Helpers._

class RequestMetricsSpec extends Specification {

  val fakeApp = new GuiceApplicationBuilder()
    .in(Mode.Test)
    .build

  val requestMetrics = fakeApp.injector.instanceOf[PlayExampleRequestMetrics]

  sequential

  "request metrics" should {
    "correctly count and time requests for performance" in {
      val metric = getMetric[TimingMetric]("request_duration")

      val originalTimingCount = metric.count
      val startTimeTotal = metric.totalTimeInMillis

      status(route(fakeApp, FakeRequest(GET, "/scala-app/long")).get) must equalTo(OK)

      val counted = metric.count - originalTimingCount
      val measuredDuration = metric.totalTimeInMillis - startTimeTotal

      counted must be equalTo (1)
      measuredDuration must be >= 2000L
    }

    "correctly count OK requests" in {
      val metric = getMetric[CountMetric]("200_ok")

      val originalOkCount = metric.count

      status(route(fakeApp, FakeRequest(GET, "/scala-app")).get) must equalTo(OK)

      val counted = metric.count - originalOkCount

      counted must be equalTo (1)
    }
  }

  def getMetric[A <: Metric](metricName: String) = requestMetrics.asMetrics.find(_.name == metricName).get.asInstanceOf[A]
}