resolvers ++= Seq(
  Classpaths.typesafeResolver,
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("play" % "sbt-plugin" % "2.1-RC4")

addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.4.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.6")

