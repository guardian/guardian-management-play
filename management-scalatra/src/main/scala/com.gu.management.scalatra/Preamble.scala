package com.gu.management.scalatra

import com.gu.management.manifest.{ApplicationFileProvider, Manifest => WebAppManifest}

object Preamble {
  implicit def webappManifest2getReloadedManifestInformation(manifest: WebAppManifest) = new {
    def getReloadedManifestInformation = {
      manifest.reload
      manifest.getManifestInformation
    }
  }
}

object ManifestFactory {
  def apply(fileProvider: ApplicationFileProvider) = new WebAppManifest(fileProvider)

  def apply(fileProvider: ApplicationFileProvider, manifestFilePath: String) = {
    val m = new WebAppManifest(fileProvider)
    m setManifestFilePath manifestFilePath
    m
  }
}