
libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "0.9.27",
    "com.mongodb.casbah" %% "casbah" % "2.1.5-1"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)