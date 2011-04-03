package com.gu.management.scalatra

import com.gu.management.timing.{TimestampMetric, CountMetric, TimingMetric}
import scala.xml.PrettyPrinter
import java.text.SimpleDateFormat
import java.util.TimeZone

trait ManagementFilterWithMetricStatus extends ManagementFilterWithSwitchboard {

  protected def timingMetrics: List[TimingMetric] = List()
  protected def countMetrics: List[CountMetric] = List()
  protected def timestampMetrics: List[TimestampMetric] = List()

  protected override def managementGetUrls: Map[String, () => _] = super.managementGetUrls ++ Map(
    "/management/status" -> status _
  )

  private lazy val printer = new PrettyPrinter(80, 3)
  private lazy val dateFormatter = {
    val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    df setTimeZone (TimeZone getTimeZone "UTC")
    df
  }

  private def timingsFor(metric: TimingMetric) =
    <placeholder>
      <count>{ metric.getCount }</count>
      <totalTimeInMillis>{ metric.getTotalTimeInMillis }</totalTimeInMillis>
    </placeholder>.copy(label = metric.getManagementStatusElementName)

  private def countsFor(metric: CountMetric) =
    <placeholder>
      <count>{ metric.getCount }</count>
    </placeholder>.copy(label = metric.getManagementStatusElementName)

  private def timestampsFor(metric: TimestampMetric) =
    <placeholder>
      <timestamp>{ dateFormatter format metric.getTimeStamp }</timestamp>
    </placeholder>.copy(label = metric.getManagementStatusElementName)

  private def status() = {
    val xml =
      <status>
        { if (timingMetrics.nonEmpty) {
            <timings>
              { timingMetrics map timingsFor }
            </timings>
          }
        }
        { if (countMetrics.nonEmpty) {
            <counts>
              { countMetrics map countsFor }
            </counts>
          }
        }
        {
          val assignedTimestampMetrics = timestampMetrics filter { _.getTimeStamp != null }
          if (assignedTimestampMetrics.nonEmpty) {
            <timestamps>
              { assignedTimestampMetrics map timestampsFor }
            </timestamps>
          }
        }
      </status>

    response setContentType "application/xml"
    printer format xml
  }
}
