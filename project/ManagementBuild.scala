import sbt._
import sbt.Keys._
import play.Play.autoImport._
import PlayKeys._
import play._

object ManagementBuild extends Build {

  private implicit class Project2noPublish(project: Project) {
    lazy val noPublish : sbt.Project = project.settings(Seq(
      publish := {},
      publishLocal := {}
    ):_*)
  }

  lazy val root = Project("management-root", file(".")).enablePlugins(play.PlayScala).aggregate(
    managementPlay,
    examplePlay
  ).noPublish

  val specs = "org.specs2" %% "specs2" % "2.3.13" % "test"

  lazy val managementPlay = managementProject("management-play").settings(
    libraryDependencies ++= Seq(
      ws,
      filters,

      // see http://code.google.com/p/guava-libraries/issues/detail?id=1095
      "com.google.code.findbugs" % "jsr305" % "1.3.+"
    )
  )

  lazy val examplePlay = Project("example",file("example"))
    .dependsOn(managementPlay)
    .enablePlugins(play.PlayScala)
    .noPublish.settings(
      libraryDependencies += specs
    )

  def managementProject(name: String) = Project(name, file(name)).settings(Seq(
    javacOptions := Seq(
      "-g",
      "-encoding", "utf8"
    ),
    scalacOptions := Seq("-unchecked", "-optimise", "-deprecation",
      "-Xcheckinit", "-encoding", "utf8", "-feature", "-Yinline-warnings",
      "-Xfatal-warnings"
    ),
    libraryDependencies += specs
  ):_*)
}
