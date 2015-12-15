Getting Started
===============

Compatibility
-------------

Play:  2.4.6
sbt:   0.13.9
Scala: 2.11.7

Add the dependency to your build
-----------------------------------

    resolvers += "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-releases"
    libraryDependencies += "com.gu" %% "management-play" % "8.0"

Look at the example!
-----------------------

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
    libraryDependencies += "com.gu" %% "management-play" % "8.0"

Add to the play configuration
-----------------------------

Add the following line to `conf/application.conf`:

    play.modules.enabled += "com.gu.management.play.InternalManagementModule"

Bind the management pages
-------------------------

The module must have the pages and application name set in your Global onStart function.

Create a scala Object that mixes in the `com.gu.management.play.Management` trait to
provide the list of pages and your application name to the plugin:

```scala
package conf

object YourApplicationManagement extends Management {
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

If Global.scala doesn't exist in your project then create it with the 
skeleton provided below. Otherwise add a call to `InternalManagementServer.start(...)`
to your onStart method.

```scala
import com.gu.management.play.InternalManagementServer
import conf.YourApplicationManagement
import play.api.Application

object Global {
  override def onStart(app: Application): Unit = {
    InternalManagementServer.start(app, YourApplicationManagement)
  }
}
```

