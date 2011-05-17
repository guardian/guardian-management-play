import java.io.File
import sbt._

class ManagementProject(info: ProjectInfo) extends ParentProject(info) {

  val guardianGithub = "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"

  lazy val core = project("management-core", "management-core", new Core(_))

  lazy val spring = project("management-spring", "management-spring", new Spring(_), core)

  lazy val guice = project("management-guice", "management-guice", new Guice(_), core)

  lazy val scalatra = project("management-scalatra", "management-scalatra", new Scalatra(_), core)



  class Core(info: ProjectInfo) extends DefaultProject(info)
    with JavaProject  {

    val log4j = "log4j" % "log4j" % "1.2.14"
    val commonsIo = "commons-io" % "commons-io" % "1.4"
    val commonsLang = "commons-lang" % "commons-lang" % "2.4"

    val guava = "com.google.guava" % "guava" % "r09"

    val springTest = "org.springframework" % "spring-test" % "3.0.0.RELEASE" % "test"
    val springCore = "org.springframework" % "spring-core" % "3.0.0.RELEASE" % "test"

    // don't be tempted to upgrade this to 2.5: the version of resin we use
    // only supports servlet api 2.4!
    val servlet = "javax.servlet" % "servlet-api" % "2.4" % "provided"
  }


  class Spring(info: ProjectInfo) extends DefaultProject(info)
    with JavaProject {
    val springVersion = "3.0.0.RELEASE"

    val springWebMvc = "org.springframework" % "spring-webmvc" % springVersion
    val springJdbc = "org.springframework" % "spring-jdbc" % springVersion

    val hibernate = "org.hibernate" % "hibernate-core" % "3.3.2.GA"
    val c3p0 = "c3p0" % "c3p0" % "0.9.1.2"

    val cglib = "cglib" % "cglib-nodep" % "2.1_3"
    val httpClient = "commons-httpclient" % "commons-httpclient" % "3.0.1"

    val servlet = "javax.servlet" % "servlet-api" % "2.4" % "provided"
  }


  class Guice(info: ProjectInfo) extends DefaultProject(info)
    with JavaProject {

    val guice = "com.google.inject" % "guice" % "2.0"
    val guiceServlet = "com.google.inject.extensions" % "guice-servlet" % "2.0"

    val servlet = "javax.servlet" % "servlet-api" % "2.4" % "provided"
  }

  class Scalatra(info: ProjectInfo) extends DefaultProject(info) {

    val scalatra = "org.scalatra" %% "scalatra" % "2.0.0.M3"

    val slf4jApi = "org.slf4j" % "slf4j-api" % "1.6.1"

    val guardianConf = "com.gu" % "configuration" % "2.9"

    val servlet = "javax.servlet" % "servlet-api" % "2.4" % "provided"
  }


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

  override def managedStyle = ManagedStyle.Maven

  val publishTo =
    if (projectVersion.value.toString.contains("-SNAPSHOT"))
      Resolver.file("guardian github snapshots", new File(System.getProperty("user.home")
            + "/guardian.github.com/maven/repo-snapshots"))
    else
      Resolver.file("guardian github releases", new File(System.getProperty("user.home")
            + "/guardian.github.com/maven/repo-releases"))
}
