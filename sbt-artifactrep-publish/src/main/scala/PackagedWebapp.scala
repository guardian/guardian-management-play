import sbt._
import com.gu.versioninfo.VersionInfo

trait PackagedWebapp extends BasicWebScalaProject with MavenStyleWebScalaPaths with VersionInfo {
  val deployCommon = "com.gu" % "deploy-common" % "1.35" % "runtime"
  val distWorkingDir = outputPath / "dist" / "build"

  lazy val prepareDist = prepareDistAction dependsOn(`package`) describedAs "lay out distribution folder"

  def prepareDistAction = task {

    // unzip deploy common to deploy directory
    val deployCommonFile = "lib_managed" ** "deploy-common-*.jar"
    FileUtilities.unzip(deployCommonFile.get.toList.head, distWorkingDir / "deploy", log)

    // copy war to "war" subdirectory
    FileUtilities.copyFile(outputPath / defaultWarName, distWorkingDir / "war" / ( projectName.value + ".war" ), log)

    // copy cmdline jmxclient to lib/deploytime
    val jxmClientFile = "lib_managed" ** "cmdline-jmxclient-*.jar"
    FileUtilities.createDirectory(distWorkingDir / "lib" / "deploytime", log)
    FileUtilities.copyFlat(jxmClientFile.get, distWorkingDir / "lib" / "deploytime", log)

    None
  }

  // task to create a distribution zip ready for deployment
  lazy val dist = distAction dependsOn(prepareDist) describedAs "build zip file suitable for deployment"
  def distAction = zipTask( (distWorkingDir ##) ***, outputPath / "dist", "dist.zip")

  lazy val publishDist = publishDistAction dependsOn (dist) describedAs "copy dist zip file to artifact repository"


  val distOutputDir = Path.fromFile("/r2/ArtifactRepository") / projectName.value / "trunk" / buildName

  def publishDistAction = task {
    FileUtilities.createDirectory(distOutputDir, log)
    FileUtilities.copyFile( (outputPath / "dist" ##) / "dist.zip", distOutputDir / "artifacts.zip", log)
  }
}
