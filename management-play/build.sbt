
libraryDependencies ++= Seq(
    "com.gu" %% "management" % "5.25",
    "com.gu" %% "management-internal" % "5.25",
    "com.gu" %% "management-logback" % "5.25",
    "play" %% "play" % "2.1-RC4",
    "org.reflections" % "reflections" % "0.9.8" exclude("javassist", "javassist"), // http://code.google.com/p/reflections/issues/detail?id=140
    "org.specs2" %% "specs2" % "1.13" % "test",
    "play" %% "play-test" % "2.1-RC4" % "test"
)

// needed for Play
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

