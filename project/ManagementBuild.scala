import sbt._

object ManagementBuild extends Build {
  lazy val root = Project("root", file(".")) aggregate(management, managementLogback, example)

  lazy val management = Project("management", file("management"))

  lazy val managementLogback = Project("management-logback", file("management-logback")) dependsOn (management)

  lazy val example = Project("example", file("example")) dependsOn (management, managementLogback)
}
