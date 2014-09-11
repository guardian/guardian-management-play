
libraryDependencies ++= Seq(
    "com.gu" %% "management" % "5.33",
    "com.gu" %% "management-internal" % "5.33",
    "com.gu" %% "management-logback" % "5.33",
    "org.reflections" % "reflections" % "0.9.8" exclude("javassist", "javassist"), // http://code.google.com/p/reflections/issues/detail?id=140
    "org.specs2" %% "specs2" % "1.13" % "test",
    "com.typesafe.play" %% "play" % "2.2.0",
    "com.typesafe.play" %% "play-test" % "2.2.0" % "test"
)

// needed for Play
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

// disable publishing the main javadoc jar
publishArtifact in (Compile, packageDoc) := false

