package conf

import com.gu.management.play.{ Management => GuManagement }
import scala.concurrent.{Future, Await}
import com.gu.management.{ErrorResponse, ManagementPage, HttpRequest, PlainTextResponse}
import scala.concurrent.duration._
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.Play.current

class UrlPagesHealthcheckManagementPage @Inject() (wsClient: WSClient, val urls: String*) extends ManagementPage {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  override val path = "/management/healthcheck"

  val base = "http://localhost:9000"

  override def get(req: HttpRequest) = {

    def fetch(url: String) = wsClient.url(url).withHeaders("X-Gu-Management-Healthcheck" -> "true").get()

    val checks = urls map { base + _ } map { url => fetch(url).map{ response => url -> response } }
    val sequenced = Future.sequence(checks)
    val failed = sequenced map { _ filter { _._2.status / 100 != 2 } }

    Await.result(failed, 10 -> SECONDS) match {
      case Nil =>
        PlainTextResponse("OK")

      case failures =>
        val message = failures map { case (url, response) => s"FAIL: $url (${response.status}})" }
        ErrorResponse(503, message mkString "\n")
    }
  }
}