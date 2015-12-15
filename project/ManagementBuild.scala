import play.sbt.PlayScala
import sbt._
import sbt.Keys._
import play.sbt.PlayImport.ws

object ManagementBuild extends Build {

  private implicit class Project2noPublish(project: Project) {
    lazy val noPublish = project.settings(Seq(
      publish := {},
      publishLocal := {}
    ):_*)
  }

  lazy val root = Project("management-root", file(".")).enablePlugins(PlayScala).aggregate(
    managementPlay,
    examplePlay
  ).noPublish

  val specs = "org.specs2" %% "specs2" % "2.3.13" % "test"

  lazy val guardianResolver = resolvers += "Guardian Github" at "http://guardian.github.com/maven/repo-releases"

  lazy val managementPlay = managementProject("management-play").settings(guardianResolver).settings(
    libraryDependencies ++= Seq(
      ws,
      // see http://code.google.com/p/guava-libraries/issues/detail?id=1095
      "com.google.code.findbugs" % "jsr305" % "1.3.+"
    )
  )

  lazy val examplePlay = Project(
    "example",
    file("example")
  ).enablePlugins(PlayScala)
    .dependsOn(managementPlay)
    .settings(
      guardianResolver,
      libraryDependencies += specs
    )
    .noPublish

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
