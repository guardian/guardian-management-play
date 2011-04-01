package com.gu.management.scalatra

import org.scalatra.ScalatraFilter
import com.gu.management.scalatra.Preamble._
import com.gu.management.manifest.ApplicationFileProvider

trait ManagementFilter extends ScalatraFilter {

  protected lazy val managementUrls: Map[String, () => _] = Map(
    "/management/healthcheck" -> healthcheck _,
    "/management/manifest" -> manifest _
  )

  protected lazy val fileProvider = new ApplicationFileProvider

  protected lazy val manifestList = List(
    ManifestFactory(fileProvider), ManifestFactory(fileProvider, "version.txt")
  )

  //protected def managementUrls = { "TODO"}

  protected def manifest() = {
    manifestList map { _.getReloadedManifestInformation } mkString "\n"
  }

  protected def healthcheck() = "OK"

  //protected def properties = {
  ////@Singleton class PropertiesServlet @Inject() (configuration: Configuration) extends ScalatraServlet {
  ////  get("/*") {
  ////    configuration
  ////  }
  ////}
  //  "TODO"
  //}

  //protected def status = { "TODO"}

  //protected def switchboard = { "TODO"}


  for (url <- managementUrls.keys) {
    get(url) { managementUrls(url)() }
  }

  //get("/management") { managementUrls }
  //get("/management/properties") { properties }
  //get("/management/status") { status }
  //get("/management/switchboard") { switchboard }
}