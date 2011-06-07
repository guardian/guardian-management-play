

libraryDependencies += "javax.servlet" % "servlet-api" % "2.4" % "provided"

// include web plugin settings in this project
seq(WebPlugin.webSettings :_*)


libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "7.3.1.v20110307" % "jetty"
