package com.gu.management

import io.Source
import javax.servlet.http.HttpServletRequest

object ManifestPage extends ManagementPage {

  val path = "/management/manifest"

  def get(req: HttpServletRequest) = response

  lazy val response = PlainTextResponse(
    Option(getClass.getResourceAsStream("/version.txt"))
            .map(Source.fromInputStream(_))
            .map(_.mkString)
            .getOrElse("Could not find version.txt on classpath.  Did you include the sbt-version-info-plugin?")
  )

}
