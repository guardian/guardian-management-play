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
import java.io.File
import java.io.FileInputStream
import java.util.jar.Manifest

abstract class DistributableElement {
  protected def filter(simplePattern: String): NameFilter = GlobFilter(simplePattern)
  def addToDistribution(where: Path, logger: Logger): Unit
}

case class DeploySupport(src: Path, deployLib: Path) extends DistributableElement {
  override def addToDistribution(where: Path, logger: Logger) {
    FileUtilities.copyDirectory(src, where / "deploy", logger)
    FileUtilities.unzip(deployLib, where / "deploy", logger)
  }
}

case class Paths(src: Path, application: String, to: String) extends DistributableElement {
  override def addToDistribution(where: Path, logger: Logger) {
    val paths = (src ##) ** filter("*")
    FileUtilities.copy(paths.get, where / application / to, logger)
  }
}

case class PathsFlat(src: Path, application: String) extends DistributableElement {
  override def addToDistribution(where: Path, logger: Logger) {
    val paths = (src ##) ** filter("*")
    FileUtilities.copy(paths.get, where / application, logger)
  }
}

case class WebApp(src: Path, application: String, warName: String) extends DistributableElement {
  override def addToDistribution(where: Path, logger: Logger) {
    FileUtilities.copyFile(src, where / application / "webapps" / warName, logger)
  }
}

case class RebundledWebApp(src: Path, extra: Iterable[Path], application: String, warName: String) extends DistributableElement {
  override def addToDistribution(where: Path, logger: Logger) {
    FileUtilities.doInTemporaryDirectory(logger) { file: File =>
      val tmp = Path.fromFile(file)

      FileUtilities.unzip(src, tmp / "jar", logger)
      FileUtilities.copy(extra, tmp / "jar", logger)

      val jar = (tmp / "jar" ##) ** filter("*") filter { !_.isDirectory }

      val manifestFile = (jar ** filter("MANIFEST.MF")).get.toList.head
      val jarNoManifest = jar --- manifestFile

      val manifest = new Manifest(new FileInputStream(manifestFile.asFile))

      FileUtilities.jar(jarNoManifest.get, tmp / warName, manifest, true, logger)
      FileUtilities.copyFile(tmp / warName, where / application / "webapps" / warName, logger)

      Right(tmp / warName)
    }
  }
}