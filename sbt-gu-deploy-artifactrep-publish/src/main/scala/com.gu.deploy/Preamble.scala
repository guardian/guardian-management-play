/*

   Copyright 2010 Guardian News and Media

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/
package com.gu.deploy

import sbt._

object Preamble {

  implicit def pathFinder2head(finder: PathFinder) = new {
    def head = finder.get.toList.head
  }

  implicit def project2Get(project: Project) = new {
    def get(filePattern: String) = (project.outputPath ** project.filter(filePattern)).head
  }

  implicit def mavenStyleScalaPaths2src(project: MavenStyleScalaPaths) = new {
    def src(filePattern: String) = (project.sourcePath ** project.filter(filePattern)).head
  }

  implicit def basicDependencyPaths2Lib(project: BasicDependencyPaths) = new {
    def lib(filePattern: String) = (project.path(project.managedDirectoryName) ** project.filter(filePattern)).head
  }

}
