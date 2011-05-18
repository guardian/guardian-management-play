package com.gu.management

import net.liftweb.http.Req

class ManagementIndex(mgtPages: Seq[ManagementPage]) extends XhmlManagementPage {
  val managementSubPath = Nil
  def body(r: Req) = <ul>{ for (p <- mgtPages) yield <li><a href={p.url} >{p.linktext}</a></li> }</ul>
  val title = "Management URLs"

}