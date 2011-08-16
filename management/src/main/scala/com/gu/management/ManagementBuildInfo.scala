package com.gu.management


object ManagementBuildInfo {
  lazy val version = Option(getClass.getPackage.getImplementationVersion) getOrElse "DEV"
}