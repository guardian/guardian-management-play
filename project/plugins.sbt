resolvers ++= Seq(
  Classpaths.typesafeReleases,
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8")

