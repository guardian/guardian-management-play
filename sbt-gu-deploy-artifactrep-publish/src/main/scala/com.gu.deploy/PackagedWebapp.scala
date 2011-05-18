/*

   Copyright 2010 Guardian News and Media

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/
package com.gu.deploy

import sbt._
import com.gu.versioninfo.VersionInfo
import com.gu.deploy.Preamble._

trait PackagedWebapp extends BasicWebScalaProject with VersionInfo {

  val guardian_nexus = "Guardian Nexus" at "http://nexus.gudev.gnl:8081/nexus/content/groups/public"
  def deployLibsVersion = "1.55"
  val deployLibs = "com.gu" % "gu-deploy-libs" % deployLibsVersion % "runtime"

  protected def artifact = projectName.value
  protected def deployLibJar = this lib "gu-deploy-libs-*.jar"

  private val distDir = outputPath / "dist"
  private val distWorkingDir = distDir / "build"
  private val artifactsDistName = "artifacts.zip"

  private def artifactrep =
    if (isDev) {
      outputPath / "DEV" / "r2" / "ArtifactRepository"
    } else {
      Path fromFile "/r2/ArtifactRepository"
    }

  lazy val distOutputDir = artifactrep / artifact / build / buildName

  def distributableElements: List[DistributableElement]

  lazy val prepareDist = prepareDistAction dependsOn(`package`) describedAs "Lay out distribution folder"
  def prepareDistAction = task {
    FileUtilities.clean(distWorkingDir, log)
    distributableElements foreach { _.addToDistribution(distWorkingDir, log) }

    None
  }

  lazy val dist = distAction dependsOn (prepareDist) describedAs "Build artifact suitable for deployment"
  def distAction = zipTask( (distWorkingDir ##) ***, distDir, artifactsDistName)

  lazy val publishDist = publishDistAction dependsOn (dist) describedAs "Publish to artifact repository"
  def publishDistAction = task {
    FileUtilities.copyFile(distDir / artifactsDistName, distOutputDir / artifactsDistName, log)

    None
  }

}