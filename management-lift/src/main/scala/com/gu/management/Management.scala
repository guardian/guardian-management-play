package com.gu.management

import net.liftweb.common._
import net.liftweb.http._
import scala.xml.NodeSeq

trait ManagementPage {
  val managementSubPath: List[String]

  lazy val path = "management" :: managementSubPath

  // the backticks here make path not be a catch-all new variable,
  // but mean "match against the value of path"
  def dispatch: LiftRules.DispatchPF = {
    case r@Req(`path`, _, GetRequest) => () => Full(render(r))
  }

  def render(req: Req): LiftResponse

  def url = path.mkString("/")
  def linktext = "/" + managementSubPath.mkString("/")
}

trait XhmlManagementPage extends ManagementPage {
  final def render(r: Req) = XhtmlResponse(
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>{title}</title>
        </head>
        <body>
          <h2>{title}</h2>
          { body(r) }
        </body>
      </html>,
      Full(DocType.html5),
      Nil,
      cookies = Nil,
      code = 200, renderInIEMode = false)

  def title: String
  def body(r: Req): NodeSeq
}


/**
 * Mixin this trait if you want to support posting to your management page
 */
trait Postable extends ManagementPage {
  override def dispatch = postDispatcher orElse super.dispatch

  // the backticks here make path not be a catch-all new variable,
  // but mean "match against the value of path"
  def postDispatcher: LiftRules.DispatchPF = {
    case r@Req(`path`, _, PostRequest) => () => {
      try {
        processPost(r)
        Full(RedirectResponse(r.uri))
      } catch {
        case ex => Full(PlainTextResponse("unexpected error: " + ex.toString + "\n", 400))
      }
    }
  }

  def processPost(r: Req)
}


object Management {
  def publishWithIndex(pages: ManagementPage*): LiftRules.DispatchPF =
    pages.foldLeft(new ManagementIndex(pages).dispatch) { _ orElse _.dispatch }
  
}