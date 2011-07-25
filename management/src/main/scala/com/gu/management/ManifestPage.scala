package com.gu.management

import io.Source
import javax.servlet.http.HttpServletRequest

class ManifestPage extends ManagementPage {

  val path = "/management/manifest"

  def get(req: HttpServletRequest) = response

  lazy val response = PlainTextResponse(
    Manifest.asStringOpt.getOrElse("Could not find version.txt on classpath.  Did you include the sbt-version-info-plugin?")
  )

}

object Manifest {

  val asStreamOpt = Option(getClass.getResourceAsStream("/version.txt")).map(Source.fromInputStream(_))
  val asStringOpt = asStreamOpt.map(_.mkString)
  val asList = asStringOpt match {
    case Some(manifest) => {
      manifest.split("\n").toList
    }
    case _ => List(): List[String]
  }

  lazy val asKeyValuePairs = {
    asList.map(line => {
      val tokens = line.split(":")
      (tokens(0).trim, tokens(1).trim)
    }).toMap
  }

}
