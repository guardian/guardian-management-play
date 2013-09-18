resolvers ++= Seq(
  Classpaths.typesafeResolver,
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("play" % "sbt-plugin" % "2.1.3")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.6")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")

