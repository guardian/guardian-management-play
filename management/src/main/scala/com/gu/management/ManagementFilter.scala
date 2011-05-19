package com.gu.management

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import javax.servlet._

trait ManagementPage {
  /**
    * The path to this page
    */
  val path: String
}

trait ManagementFilter extends Filter {
  def init(filterConfig: FilterConfig) {}

  def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val httpReq = request.asInstanceOf[HttpServletRequest]
    val httpResp = response.asInstanceOf[HttpServletResponse]

    val path = Option(httpReq.getContextPath).getOrElse("") + Option(httpReq.getServletPath).getOrElse("")

    println("path is " + path)
  }

  def destroy() {}

  val pages: List[ManagementPage]

}