resolvers ++= Seq(
  "Web plugin repo" at "http://siasia.github.com/maven2",
  "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
)

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

libraryDependencies += "com.github.siasia" %% "xsbt-web-plugin" % "0.11.2-0.2.11"