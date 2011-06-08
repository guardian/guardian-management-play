package com.gu.management

import javax.servlet.http.HttpServletResponse
import xml.Elem

/**
 * Parent of all (immutible) response objects
 *
 * Note that this pattern is heavly inspired by lift's excellent
 * wrappers around the response.
 */
trait Response {
  def writeTo(resp: HttpServletResponse)

}


case class PlainTextResponse(text: String) extends Response {
  def writeTo(resp: HttpServletResponse) {
    resp.setCharacterEncoding("UTF-8")
    resp.setContentType("text/plain")
    resp.getWriter.println(text)
  }
}


case class HtmlResponse(response: Elem) extends Response {
  def writeTo(resp: HttpServletResponse) {
    resp.setCharacterEncoding("UTF-8")
    resp.setContentType("application/xhtml+xml")
    resp.getWriter.println(response.toString())
  }
}

case class XmlResponse(response: Elem) extends Response {
  def writeTo(resp: HttpServletResponse) {
    resp.setCharacterEncoding("UTF-8")
    resp.setContentType("application/xml")
    resp.getWriter.println(response.toString())
  }
}


case class ErrorResponse(code: Int, msg: String) extends Response {
  def writeTo(resp: HttpServletResponse) {
    resp.sendError(code, msg)
  }
}

case class RedirectResponse(to: String) extends Response {
  def writeTo(resp: HttpServletResponse) {
    resp.setHeader("Location", to)
    resp.setStatus(302)
  }
}
