package com.gu.management.scalatra

import com.gu.conf.Configuration
import com.gu.management.scalatra.Preamble._

import org.scalatra.ScalatraFilter

trait ManagementFilter extends ScalatraFilter {

  protected def managementGetUrls: Map[String, () => _] = Map(
    "/management/manifest" -> manifest _,
    "/management/properties" -> properties _
  )

  protected def managementPostUrls: Map[String, () => _] = Map()

  protected def configuration: Configuration

  protected lazy val fileProvider = ApplicationFileProviderFactory(servletContext)

  protected def manifestList = List(
    ManifestFactory(fileProvider, "/WEB-INF/classes/version.txt"), ManifestFactory(fileProvider)
  )

  protected def manifest() = manifestList map { _.getReloadedManifestInformation } mkString "\n"
  protected def properties() = configuration.toString

  //protected def status = { "TODO"}
  //get("/management/status") { status }

  private def managementUrlLink(url: String) = <a href={ url.replace("^/", "") }>{ url.replace("^/management", "") }</a>

  get("/management") {
    <html>
      <head>
        <title>Management URLs</title>
      </head>
      <body>
        <h2>Management URLs</h2>
        <ul>
          { managementGetUrls.keys.toList.sorted map { url => <li>{ managementUrlLink(url) }</li> } }
        </ul>
      </body>
    </html>
  }

  for (url <- managementGetUrls.keys) {
    get(url) { managementGetUrls(url)() }
  }

  for (url <- managementPostUrls.keys) {
    post(url) { managementPostUrls(url)() }
  }

}