package com.gu.management.play

import play.api.{Configuration, Environment, Mode}
import org.specs2.mutable.{BeforeAfter, Specification}
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
    "be created" in new Context {
      running(FakeApplication()) {
        configuredAppBuilder.injector.instanceOf[InternalManagementServer].
          aka("Internal Management Server") must beLike {
          case server: InternalManagementServer => ok
        }
      }
    }
    "start management server in a fixed port" in new Context {
      running(FakeApplication()) {
        InternalManagementServer.start(configuredAppBuilder, TestManagement, 18000)
        ManagementServer.isRunning must beTrue
      }
    }
    "start management server in an auto port" in new Context {
      running(FakeApplication()) {
        InternalManagementServer.start(configuredAppBuilder, TestManagement)
        ManagementServer.isRunning must beTrue
      }
    }
    "serve management page" in new Context {
      running(FakeApplication()) {
        InternalManagementServer.start(configuredAppBuilder, TestManagement)
        val port = ManagementServer.port
        val response = Source.fromURL(s"http://localhost:$port/management/test") mkString ""
        response must be equalTo "response"
      }
    }
  }

  trait Context extends BeforeAfter {
    def before: Any = {}
    def after: Any = ManagementServer.shutdown()
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
