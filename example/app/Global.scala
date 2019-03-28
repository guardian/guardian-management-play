import com.gu.management.play.InternalManagementServer
import conf.{ExampleManagement, PlayExampleRequestMetrics}
import play.api.Application
import play.api.http.HttpFilters
import javax.inject.{Singleton, Inject}

class Filters @Inject() (metrics: PlayExampleRequestMetrics) extends HttpFilters {
  override def filters = metrics.asFilters
}

@Singleton class Global @Inject() (app: Application, management: ExampleManagement) {
  InternalManagementServer.start(app, management)
}
