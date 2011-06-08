package com.gu.management

import org.slf4j.LoggerFactory

trait Loggable {
  implicit lazy val logger = LoggerFactory.getLogger(getClass)
}