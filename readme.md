Guardian Management
===================

This project contains various helpers to ease administrative management
of production java apps.

Our policy is that each app exposes its user facing pages on a sub url,
and administrative pages on /management. So, for example, content-api.war
when deployed to a container has the actual api under /content-api/api and
the management pages on /content-api/management.

The /management url should return a html page that links to all management
pages.

This simple framework aims to make it simple to generate the standard management
pages and easy to create new app-specific ones.


Getting Started
---------------

The management pages are web framework agnostic: they use their own mini
framework, blatently inspired/ripped off from lift.

1. Add the management filter to your web.xml

To avoid any conflict with your choice of web framework, the managment
pages are implemented as a filter. So, for example:

    <!DOCTYPE web-app PUBLIC
            "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
            "http://java.sun.com/dtd/web-app_2_3.dtd" >

    <web-app>

        <filter>
            <filter-name>managementFilter</filter-name>
            <filter-class>com.gu.management.example.ScalaManagementFilter</filter-class>
        </filter>

        <filter-mapping>
            <filter-name>managementFilter</filter-name>
            <url-pattern>/management/*</url-pattern>
        </filter-mapping>

    </web-app>

The filter-class is a class that you are going to implement.

2. Implement the filter class

Your filter class should derive from `com.gu.management.ManagementFilter` and implement
the pages member:

    class ScalaManagementFilter extends ManagementFilter {
      lazy val pages =
        DummyPage ::
        ManifestPage ::
        new Switchboard(Switches.all) ::
        new StatusPage(TimingMetrics.all) ::
        Nil
    }