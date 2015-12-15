package com.gu.management.play

import play.api.Application
import play.api.mvc.{ AnyContent, Request }
import com.gu.management.ListMultiMaps
import scala.collection.JavaConversions._

object `package` extends ListMultiMaps {

  implicit class Request2Parameters[A](request: Request[A]) {
    lazy val parameters: ListMultiMap[String, String] = {
      val queryStringParameters = request.queryString mapValues { _.toList }
      val bodyFormParameters: Map[String, List[String]] = request.body match {
        case body: AnyContent if body.asFormUrlEncoded.isDefined => body.asFormUrlEncoded.get mapValues { _.toList }
        case _ => Map()
      }

      queryStringParameters addBindings bodyFormParameters
    }
  }

  implicit class Request2RequestURI[A](request: Request[A]) {
    lazy val requestURI: String = request.uri.replaceAll("\\?.*", "")
  }

  implicit class Application2GetConfigurationProperty(app: Application) {
    def getConfigurationProperty(key: String, default: String): String = {
      app.configuration.getString(key).getOrElse(default)
    }
  }

}
