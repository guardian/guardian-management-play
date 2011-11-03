
libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.1",
    "net.liftweb" %% "lift-json" % "2.4-M4",
    "javax.servlet" % "servlet-api" % "2.4" % "provided",
    "org.specs2" %% "specs2" % "1.5" % "test"
)

// needed for specs2
resolvers += ScalaToolsSnapshots

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

