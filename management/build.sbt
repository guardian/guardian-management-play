import java.util.jar._

name := "management"

organization := "com.gu"

// try adding "in ThisBuild" here
version := "4.0"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.1"

libraryDependencies += "javax.servlet" % "servlet-api" % "2.4" % "provided"

libraryDependencies += "org.specs2" %% "specs2" % "1.2" % "test"

packageOptions <+= (version, name) map { (v, n) =>
  Package.ManifestAttributes(
    Attributes.Name.IMPLEMENTATION_VERSION -> v,
    Attributes.Name.IMPLEMENTATION_TITLE -> n,
    Attributes.Name.IMPLEMENTATION_VENDOR -> "guardian.co.uk"
  )
}

publishTo <<= (version) { version: String =>
    val publishType = if (version.endsWith("SNAPSHOT")) "snapshots" else "releases"
    Some(
        Resolver.file(
            "guardian github " + publishType,
            file(System.getProperty("user.home") + "/guardian.github.com/maven/repo-" + publishType)
        )
    )
}
