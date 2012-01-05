package com.gu.management

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import javax.servlet.FilterChain
import org.slf4j.MDC

class UniqueIdFilter extends AbstractHttpFilter {

  lazy val uniqueIdHeaderName = "X-GU-UniqueId"
  lazy val uniqueIdKey = "GUUID"
  lazy val defaultId = "None"

  def doHttpFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain){
    val headerValue = request.getHeader(uniqueIdHeaderName)
    val value = if (headerValue != null) headerValue else defaultId
    MDC.put(uniqueIdKey, value)
    chain.doFilter(request, response)
  }

}