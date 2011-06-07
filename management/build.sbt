name := "management"

organization := "com.gu"

version := "4.0-SNAPSHOT"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.1"

libraryDependencies += "javax.servlet" % "servlet-api" % "2.4" % "provided"

libraryDependencies += "org.specs2" %% "specs2" % "1.2" % "test"

publishTo <<= (version) { version: String =>
    val publishType = if (version.endsWith("SNAPSHOT")) "snapshots" else "releases"
    Some(
        Resolver.file(
            "guardian github " + publishType,
            file(System.getProperty("user.home") + "/guardian.github.com/maven/repo-" + publishType)
        )
    )
}
