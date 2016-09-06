package com.gu.management.play

import com.gu.management.ManagementPage
import com.gu.management.internal._
import play.api.inject.{Binding, ApplicationLifecycle, Module}
import play.api._
import javax.inject._

import scala.concurrent.Future

trait Management {
  val applicationName: String
  val pages: List[ManagementPage]
}

class InternalManagementModule extends Module {
  def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(bind[InternalManagementServer].to[InternalManagementServerImpl]
    )
  }
}

trait InternalManagementServer {
  def startServer(applicationName: String, pages: List[ManagementPage], port: Option[Int] = None)
}

@Singleton
class InternalManagementServerImpl @Inject() (lifecycle: ApplicationLifecycle)
    extends InternalManagementServer {

  implicit val log = Logger(getClass)

  def startServer(applicationNameParam: String, pagesParam: List[ManagementPage], port: Option[Int]): Unit = {
    log.info(s"Starting internal management server for $applicationNameParam")
    val handler = new ManagementHandler {
      val applicationName = applicationNameParam
      val pages = pagesParam
    }
    port.fold(ManagementServer.start(handler))(ManagementServer.start(handler, _))
  }

  lifecycle.addStopHook { () =>
    log.info(s"Shutting down management server")
    ManagementServer.shutdown()
    Future.successful(())
  }
}

object InternalManagementServer {
  def start(app: Application, management: Management): Unit = {
    val server = app.injector.instanceOf[InternalManagementServer]
    server.startServer(management.applicationName, management.pages)
  }

  def start(app: Application, management: Management, port: Int): Unit = {
    val server = app.injector.instanceOf[InternalManagementServer]
    server.startServer(management.applicationName, management.pages, Some(port))
  }
}
