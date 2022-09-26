resolvers ++= Seq(
  Classpaths.typesafeReleases,
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.19")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8")


addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
