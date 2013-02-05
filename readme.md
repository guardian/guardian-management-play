Getting Started
===============

Add the dependency to your build
-----------------------------------

    resolvers += "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-releases"
    libraryDependencies += "com.gu" %% "management-play" % "5.21"

Look at the example!
-----------------------

See example commit https://github.com/guardian/guardian-management/commit/f801e8d0

The [example project](https://github.com/guardian/guardian-management-play/tree/master/example) has
management routes set up and uses some switches and timing metrics.

    $ git clone git@github.com:guardian/guardian-management-play.git
    $ cd guardian-management-play
    $ sbt
    > project example
    > run

Try the following URLs locally:

 * http://localhost:9000/scala-app
 * http://localhost:18080/management
 * http://localhost:18080/management/switchboard

Also, enable the `take-it-down` switch and retry `/scala-app`.


Using the internal server in Play 2
===================================

Configure your dependencies
---------------------------

    resolvers += "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-releases"
    libraryDependencies += "com.gu" %% "management-play" % "5.21"

Add to the play plugins file
----------------------------

Add the following line to conf/play.plugins (create it if it doesn't exist):

    1000:com.gu.management.play.InternalManagementPlugin

Bind the management pages
-------------------------

The plugin locates the pages and application name by convention.

Create a scala Object called conf.Management that mixes in the ManagementPageManifest trait to
provide the list of pages and your application name to the plugin:

```scala
package conf

object Management extends ManagementPageManifest {
  val applicationName = "your-application-name"

  lazy val pages = List(
    new ManifestPage,
    new HealthcheckManagementPage,
    new Switchboard(applicationName, Switches.all),
    StatusPage(applicationName, Metrics.all),
    new LogbackLevelPage(applicationName)
  )
}
```

If there is a reason you can't name your file conf.Management, you can override it
by setting management.manifestobject in application.conf.


