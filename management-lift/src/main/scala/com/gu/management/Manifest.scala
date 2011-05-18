package com.gu.management

import io.Source
import net.liftweb.http.{Req, PlainTextResponse}

object Manifest extends ManagementPage {
  val managementSubPath = "manifest" :: Nil

  def render(r: Req) = response

  lazy val response = PlainTextResponse(
    Option(getClass.getResourceAsStream("/version.txt"))
            .map(Source.fromInputStream(_))
            .map(_.mkString)
            .getOrElse("Could not find version.txt on classpath.  Did you include the sbt-version-info-plugin?")
  )

}