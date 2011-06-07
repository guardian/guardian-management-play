

libraryDependencies ++= Seq(
    "javax.servlet" % "servlet-api" % "2.4" % "provided",
    "org.slf4j" % "slf4j-simple" % "1.6.1")
    
// include web plugin settings in this project
seq(WebPlugin.webSettings :_*)

// and use this version of jetty for jetty run
libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "7.3.1.v20110307" % "jetty"


publishArtifact := false
