import com.gu.management.play.InternalManagementServer
import conf.{ExampleManagement, PlayExampleRequestMetrics}
import play.api.Application
import play.api.mvc.WithFilters

object Global extends WithFilters(PlayExampleRequestMetrics.asFilters: _*) {
  override def onStart(app: Application): Unit = {
    InternalManagementServer.start(app, ExampleManagement)
  }
}
