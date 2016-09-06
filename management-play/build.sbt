
libraryDependencies ++= Seq(
    "com.gu" %% "management" % "5.36-SNAPSHOT",
    "com.gu" %% "management-internal" % "5.36-SNAPSHOT",
    "com.gu" %% "management-logback" % "5.36-SNAPSHOT",
    "com.typesafe.play" %% "play" % "2.4.6",
    "com.typesafe.play" %% "play-test" % "2.4.6" % "test"
)

// needed for Play
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

