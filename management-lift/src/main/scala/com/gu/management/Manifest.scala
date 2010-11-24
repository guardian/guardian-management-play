package com.gu.management

import net.liftweb.http.PlainTextResponse
import io.Source

object Manifest extends ManagementPage {
  val managementSubPath = "manifest" :: Nil

  def response = PlainTextResponse(Source.fromInputStream(getClass.getResourceAsStream("/version.txt")).mkString)

}