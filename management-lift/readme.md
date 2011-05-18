management-lift
===============

This module contains helpers to implement our standard management pages
on a [lift-based][lift] application (2.2-M1 or later, on scala 2.8.0 or
2.8.1).

To use, register the required management pages using Management.publishWithIndex in your liftweb boot class, e.g.:

        LiftRules.statelessDispatchTable
          .append(Management.publishWithIndex(Manifest, Status(TimingMetrics.all), Properties))

For the manifest to work, you need to generate a /version.txt file on your classpath. You probably
want to use the sbt-version-info-plugin to do this.

You can write your own pages easily, e.g. the Properties one above looks like this:

    import net.liftweb.http._
    import com.gu.management.ManagementPage

    object Properties extends ManagementPage {
        val managementSubPath = "properties" :: Nil
        def render(r: Req) = PlainTextResponse(MyConfig.configuration.toString)
    }


[lift]: http://liftweb.net
[github]: http://github.com/guardian