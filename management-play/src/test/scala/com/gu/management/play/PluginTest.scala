package com.gu.management.play

import play.api.{Configuration, Mode, Environment}
import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import com.gu.management.internal.ManagementServer
import play.api.inject.guice.GuiceApplicationBuilder
import com.gu.management._
import scala.io.Source

object TestManagement extends Management {
  val applicationName = "test-app"
  val pages = List(
    new ManagementPage {
      def get(req: HttpRequest): Response = new PlainTextResponse("response")
      val path: String = "/management/test"
    }
  )
}

object PluginTest extends Specification {

  "plugin" should {
    "be created" in {
      configuredAppBuilder.injector.instanceOf[InternalManagementServer].
        aka("Internal Management Server") must beLike {
        case server: InternalManagementServer => ok
        }
    }
    "start management server" in {
      InternalManagementServer.start(configuredAppBuilder, TestManagement)
      ManagementServer.isRunning must beTrue
    }
    "serve management page" in {
      InternalManagementServer.start(configuredAppBuilder, TestManagement)
      val port = ManagementServer.port()
      val response = Source.fromURL(s"http://localhost:$port/management/test") mkString ""
      response must be equalTo "response"
    }
  }

  def configuredAppBuilder = {
    import scala.collection.JavaConversions.iterableAsScalaIterable

    val env = Environment.simple(mode = Mode.Test)
    val config = Configuration.load(env)
    val modules = config.getStringList("play.modules.enabled").fold(
      List.empty[String])(l => iterableAsScalaIterable(l).toList)

    new GuiceApplicationBuilder().
      configure("play.modules.enabled" -> (modules :+
        "com.gu.management.play.InternalManagementModule")).build()
  }

}
