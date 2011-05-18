package com.gu.versioninfo

import java.net.InetAddress
import java.util.Date
import sbt._

trait VersionInfo extends BasicScalaProject {
  def build = "trunk"

  lazy val buildNumberString = systemOptional[String]("build.number", "DEV").value
  lazy val buildName = "%s-build.%s".format(build, buildNumberString)

  lazy val vcsNumberString = systemOptional[String]("build.vcs.number", "DEV").value

  def versionInfo = Map(
    "Revision" -> vcsNumberString,
    "Build" -> buildNumberString,
    "Date" -> new Date().toString,
    "Built-By" -> system[String]("user.name").value,
    "Built-On" -> InetAddress.getLocalHost.getHostName)


  lazy val generateVersion = generateVersionAction

  def versionFileContents = versionInfo.map{
    case (x, y) => x + ": " + y
  }.toList.sort((a, b) => a < b)

  def generateVersionAction = task{
    val versionFile = mainCompilePath / "version.txt"
    log.info("Writing to " + versionFile + ":\n   " + versionFileContents.mkString("\n   "))
    FileUtilities.write(versionFile.asFile, versionFileContents mkString ("\n"), log)
  }

  override def compileAction = super.compileAction dependsOn generateVersion

  def isDev = buildNumberString == "DEV"
}
