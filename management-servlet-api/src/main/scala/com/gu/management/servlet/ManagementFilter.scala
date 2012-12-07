package com.gu.management.servlet

import com.gu.management.{ ManagementPage, IndexPage, ManagementBuildInfo, Loggable, UserProvider, UserCredentials }
import javax.servlet._
import javax.servlet.http.{ HttpServletResponse, HttpServletRequest }

trait ManagementFilter extends AbstractHttpFilter with Loggable {
  lazy val version = ManagementBuildInfo.version

  override def init(filterConfig: FilterConfig) {
    logger.info("Management filter v%s initialised" format version)
  }

  def doHttpFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
    val httpRequest = ServletHttpRequest(request)
    val httpResponse = ServletHttpResponse(response)

    val page = pagesWithIndex find { _.canDispatch(httpRequest) }
    page match {
      case Some(page) if page.needsAuth => request.getHeaderOption("Authorization") match {
        case Some(authString) if userProvider.isValid(extractCredentials(authString)) => page.dispatch(httpRequest).sendTo(httpResponse)
        case _ => {
          response.addHeader("WWW-Authenticate", "Basic realm=\"" + userProvider.realm + "\"")
          response.sendError(401, "Needs Authorisation")
        }
      }
      case Some(page) => page.dispatch(httpRequest).sendTo(httpResponse)
      case _ => chain.doFilter(request, response)
    }
  }

  lazy val pagesWithIndex = IndexPage(pages, applicationName, version) :: pages

  private def extractCredentials(authString: String) = {
    // authstring consists of "Basic Base64(user:pass)".
    val userAndPass = new String(javax.xml.bind.DatatypeConverter.parseBase64Binary(authString.drop(6)), "UTF-8")
    val (user, pass) = userAndPass.splitAt(userAndPass.indexWhere(_ == ':'))
    UserCredentials(user, pass.drop(1))
  }

  /**
   * Implement these members with the application name and a list of the
   * management pages you want to include
   */
  val applicationName: String
  val pages: List[ManagementPage]
  def userProvider: UserProvider
}
