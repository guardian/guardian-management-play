import java.io.File
import sbt._

class ManagementProject(info: ProjectInfo) extends ParentProject(info) {

  val guardianGithub = "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"

  // This is the "old" management-core, replaced by just "management"
  lazy val core = project("management-core", "management-core", new Core(_))

  // The "new" unified management framework
  lazy val management = project("management", "management", new Management(_))

  lazy val log4j = project("management-log4j", "management-log4j", new Log4j(_), core)

  lazy val spring = project("management-spring", "management-spring", new Spring(_), core)

  lazy val versionInfoPlugin = project("sbt-version-info-plugin", "sbt-version-info-plugin", new VersionInfoPlugin(_))

  lazy val packageDeployArtifactPlugin = project("sbt-artifactrep-publish", "sbt-artifactrep-publish",
    new PublishToArtifactrepPlugin(_), versionInfoPlugin)

  lazy val guDeployArtifactPlugin = project("sbt-gu-deploy-artifactrep-publish", "sbt-gu-deploy-artifactrep-publish",
    new GuDeployPublishToArtifactrepPlugin(_), versionInfoPlugin)

  lazy val example = project("example", "example", new Example(_), management)

  class Management(info: ProjectInfo) extends DefaultProject(info) with Servlet {
    val slf4jApi = "org.slf4j" % "slf4j-api" % "1.6.1"

    // for testing, we want log entries to display please
    val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.6.1" % "test"
  }

  val JETTY_VERSION = "7.3.1.v20110307"

  class Example(info: ProjectInfo) extends DefaultWebProject(info) with Servlet {
    val jettyWebapp = "org.eclipse.jetty" % "jetty-webapp" % JETTY_VERSION % "test"
    val slf4jsimple = "org.slf4j" % "slf4j-simple" % "1.6.1" % "test"
  }

  class Core(info: ProjectInfo) extends DefaultProject(info)
    with JavaProject with Servlet {

    val commonsIo = "commons-io" % "commons-io" % "1.4"
    val commonsLang = "commons-lang" % "commons-lang" % "2.4"

    val slf4jApi = "org.slf4j" % "slf4j-api" % "1.6.1"
    val guava = "com.google.guava" % "guava" % "r09"

    val springTest = "org.springframework" % "spring-test" % "3.0.0.RELEASE" % "test"
    val springCore = "org.springframework" % "spring-core" % "3.0.0.RELEASE" % "test"
  }

  class Log4j(info: ProjectInfo) extends DefaultProject(info)
    with JavaProject with Servlet {
    val log4j = "log4j" % "log4j" % "1.2.14"
  }


  class Spring(info: ProjectInfo) extends DefaultProject(info)
    with JavaProject with Servlet {
    val springVersion = "3.0.0.RELEASE"

    val springWebMvc = "org.springframework" % "spring-webmvc" % springVersion
    val springJdbc = "org.springframework" % "spring-jdbc" % springVersion

    val hibernate = "org.hibernate" % "hibernate-core" % "3.3.2.GA"
    val c3p0 = "c3p0" % "c3p0" % "0.9.1.2"

    val cglib = "cglib" % "cglib-nodep" % "2.1_3"
    val httpClient = "commons-httpclient" % "commons-httpclient" % "3.0.1"
  }

  class VersionInfoPlugin(info: ProjectInfo) extends PluginProject(info)
  class PublishToArtifactrepPlugin(info: ProjectInfo) extends PluginProject(info)
  class GuDeployPublishToArtifactrepPlugin(info: ProjectInfo) extends PluginProject(info)



  trait JavaProject extends BasicScalaProject with JavaTesting {
    // this isn't a scala project, so no need to include the
    // scala version number in the artifactId etc
    override def disableCrossPaths = true

    override def javaCompileOptions = super.javaCompileOptions ++
      javaCompileOptions("-Xlint:unchecked,deprecation", "-g")

    override def ivyUpdateLogging = UpdateLogging.Full
  }


  trait JavaTesting {
    val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.1" % "test"
    val junit = "junit" % "junit" % "4.7" % "test"
    val mockito = "org.mockito" % "mockito-all" % "1.8.1" % "test"

    // compatibility layer to get junit tests running under sbt
    val bryanjswift = "Bryan J Swift Repository" at "http://repos.bryanjswift.com/maven2/"
    val junitInterface = "com.novocode" % "junit-interface" % "0.4.0" % "test"
  }

  trait Servlet {
    // don't be tempted to upgrade this to 2.5: the version of resin we use
    // only supports servlet api 2.4!
    val servlet = "javax.servlet" % "servlet-api" % "2.4" % "provided"
  }


  override def managedStyle = ManagedStyle.Maven

  val publishTo =
    if (projectVersion.value.toString.contains("-SNAPSHOT"))
      Resolver.file("guardian github snapshots", new File(System.getProperty("user.home")
            + "/guardian.github.com/maven/repo-snapshots"))
    else
      Resolver.file("guardian github releases", new File(System.getProperty("user.home")
            + "/guardian.github.com/maven/repo-releases"))
}



trait PublishSources extends BasicScalaProject with BasicPackagePaths {
  lazy val sourceArtifact = Artifact.sources(artifactID)
  override def packageSrcJar = defaultJarPath("-sources.jar")
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)
}

