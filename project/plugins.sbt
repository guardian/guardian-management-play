resolvers ++= Seq(
  Classpaths.typesafeResolver,
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8")

