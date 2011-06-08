package com.gu.management.request

import java.net.{UnknownHostException, InetAddress}
import javax.servlet.http.HttpServletResponse

object AppServerHeader {
  def apply() = "X-GU-jas" -> (hostname + "!" + threadname)

  lazy val hostname = try {
      InetAddress.getLocalHost.getHostName.toLowerCase
    } catch {
      case e: UnknownHostException => "<unknown>"
    }

  def threadname = Thread.currentThread().getName
}

