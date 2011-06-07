import sbt._

object ManagementBuild extends Build {
  lazy val root = Project("root", file(".")) aggregate(management, example)

  lazy val management = Project("management", file("management"))

  lazy val example = Project("example", file("example")) dependsOn (management)
}
