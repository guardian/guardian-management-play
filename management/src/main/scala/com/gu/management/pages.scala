package com.gu.management

import javax.servlet.http.HttpServletRequest
import xml.NodeSeq
import net.liftweb.json.Extraction

object ServletRequestMatchers {
  object Path {
    def unapply(r: HttpServletRequest) =
      Some(Option(r.getServletPath).getOrElse("") + Option(r.getPathInfo).getOrElse(""))
  }

  object GET {
    def unapply(r: HttpServletRequest) =
      Option(r).filter(_.getMethod equalsIgnoreCase "GET")
  }

  object POST {
    def unapply(r: HttpServletRequest) =
      Option(r).filter(_.getMethod equalsIgnoreCase "POST")
  }
}


abstract class ManagementPage {
  /**
    * The path to this page. You should include the full
    * servlet path including /management
    */
  val path: String

  /**
    * Process a get request to this page
   */
  def get(req: HttpServletRequest): Response

  // You probably don't need to override this one unless you're doing something
  // very funky.
  import ServletRequestMatchers._
  def dispatch: PartialFunction[HttpServletRequest, Response] = {
    case r @ GET(Path(p)) if p equalsIgnoreCase path => get(r)
  }

  def url = path.dropWhile('/' ==)
  def linktext = path
}

trait JsonManagementPage extends ManagementPage {
  implicit val formats = net.liftweb.json.DefaultFormats

  def get(req: HttpServletRequest) = JsonResponse(Extraction.decompose(jsonObj))

  def jsonObj: Any
}

/**
 * Mixin this trait if you want to support posting to your management page
 */
trait Postable extends ManagementPage {
  import ServletRequestMatchers._
  def post(r: HttpServletRequest)

  override def dispatch = super.dispatch orElse {
    case r @ POST(Path(p)) if p equalsIgnoreCase path =>
      post(r)
      RedirectResponse(r.getRequestURI)
  }
}


abstract class HtmlManagementPage extends ManagementPage {
  final def get(req: HttpServletRequest) = HtmlResponse(
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>{title}</title>
        </head>
        <body>
          <h2>{title}</h2>
          { body(req) }
        </body>
      </html>)

  def title: String
  def body(r: HttpServletRequest): NodeSeq
}





