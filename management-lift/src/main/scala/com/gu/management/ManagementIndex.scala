package com.gu.management

class ManagementIndex(mgtPages: Seq[ManagementPage]) extends XhmlManagementPage {
  val managementSubPath = Nil
  def body = <ul>{ mgtPages.map(p => <li><a href={p.url} >{p.linktext}</a></li>) }</ul>
  val title = "Management URLs"

}