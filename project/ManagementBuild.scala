import sbt._

object ManagementBuild extends Build {
  lazy val root = Project("root", file(".")) aggregate(management, managementLogback, example)

  lazy val management = managementProject("management")

  lazy val managementLogback = managementProject("management-logback") dependsOn (management)

  lazy val example = managementProject("example") dependsOn (management, managementLogback)



  def managementProject(name: String) = Project(name, file(name))
}
