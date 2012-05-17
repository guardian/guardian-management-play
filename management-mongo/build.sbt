
libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "0.9.27",
    "org.mongodb" % "mongo-java-driver" % "2.7.3",
    "com.mongodb.casbah" %% "casbah" % "2.1.5-1"
)

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

seq(scalariformSettings: _*)