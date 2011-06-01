package com.gu.management.example

import com.gu.management._
import javax.servlet.http._

// example of creating your own new page type
class DummyPage extends ManagementPage {
  val path = "/management/dummy"
  def get(req: HttpServletRequest) = PlainTextResponse("Hello dummy!")
}

// switches
object Switches {
  val omniture = new DefaultSwitch("omniture", "enables omniture java script")
  val takeItDown = new DefaultSwitch("take-it-down", "enable this switch to take the site down", initiallyOn = false)

  val all = omniture :: takeItDown :: Healthcheck.switch :: Nil
}

// timing stuff
object TimingMetrics {
  val downtime = new TimingMetric("downtime")
  val requests = new TimingMetric("requests")

  val all = downtime :: requests :: Nil
}

class ScalaManagementFilter extends ManagementFilter {
  lazy val pages =
    new DummyPage() ::
    new ManifestPage() ::
    new Switchboard(Switches.all) ::
    new StatusPage(TimingMetrics.all) ::
    new HealthcheckManagementPage() ::
    Nil
}