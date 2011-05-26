package com.gu.management.example

import com.gu.management._
import javax.servlet.http._

// example of creating your own new page type
object DummyPage extends ManagementPage {
  val path = "/management/dummy"
  def get(req: HttpServletRequest) = PlainTextResponse("Hello dummy!")
}

// switches
object Switches {
  object omniture extends DefaultSwitch("omniture", "enables omniture java script")
  object takeItDown extends DefaultSwitch("take-it-down", "enable this switch to take the site down", initiallyOn = false)

  val all = omniture :: takeItDown :: Nil
}

// timing stuff
object TimingMetrics {
  object downtime extends TimingMetric("downtime")

  val all = downtime :: Nil
}

class ScalaManagementFilter extends ManagementFilter {
  lazy val pages =
    DummyPage ::
    ManifestPage ::
    new Switchboard(Switches.all) ::
    new StatusPage(TimingMetrics.all) ::
    Nil
}