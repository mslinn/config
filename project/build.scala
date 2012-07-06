import sbt._
import Keys._

object Dependencies {

  object V {
    val Akka = "2.0.1"
  }

  val resolvers = Seq(
    "Typesafe Releases"    at "http://repo.typesafe.com/typesafe/releases",
    "bookishrelease"       at "http://ci-sb-1.obi.int:8081/artifactory/libs-release-local",
    "bookishsnapshot"      at "http://ci-sb-1.obi.int:8081/artifactory/libs-snapshot-local"
    //"salat"                at "http://repo.novus.com/snapshots"
  )

  val akkaActor     = "com.typesafe.akka"  %  "akka-actor"                 % V.Akka withSources()
  val akkaRemote    = "com.typesafe.akka"  %  "akka-remote"                % V.Akka withSources()
  val akkaKernel    = "com.typesafe.akka"  %  "akka-kernel"                % V.Akka withSources()
  val akkaSl4j      = "com.typesafe.akka"  %  "akka-slf4j"                 % V.Akka withSources()
  val akkaZeromq    = "com.typesafe.akka"  %  "akka-zeromq"                % V.Akka withSources()

  val zeromq        = "org.zeromq"         %  "zeromq-scala-binding_2.9.1" % "0.0.6" withSources()
  val akkaTestkit   = "com.typesafe.akka"  %  "akka-testkit"               % V.Akka           % "test" withSources()
  val scalatest     = "org.scalatest"      %  "scalatest_2.9.2"            % "1.7.1"          % "test" withSources()

  val bkshCommon    = "com.bookish"        %  "common"                     % "0.3-SNAPSHOT"   withSources()
  val logback       = "ch.qos.logback"     %  "logback-classic"            % "1.0.0"          withSources()
  val junit         = "junit"              %  "junit"                      % "4.10"           % "test"

  val allDeps = Seq(akkaActor, akkaRemote, akkaSl4j, akkaTestkit, akkaZeromq, bkshCommon, logback, junit, scalatest, zeromq)
}

object Settings extends Build {
  val Organization = "com.bookish"
  val Version      = "0.3-SNAPSHOT"
  val ScalaVersion = "2.9.1"
  val IvyXML =
      <dependencies>
        <exclude org="org.slf4j" module="slf4j-jdk14"/>
      </dependencies>

  //System.setProperty("jline.terminal", "none") // only for Windows

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version      := Version,
    scalaVersion := ScalaVersion,
    crossPaths   := false,
    ivyXML       := IvyXML
  )

  lazy val defaultSettings = buildSettings ++ Seq(
    parallelExecution in Test := false,
    resolvers ++= Dependencies.resolvers,
    scalacOptions in (Compile, console) += "-Yrepl-sync",
    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    //javacOptions  ++= Seq("-Xliqnt:unchecked", "-Xlint:deprecation"),
    javacOptions  ++= Seq("-Xlint:deprecation", "-Xcheckinit"),
    logLevel in compile := Level.Warn,
    // publishing options
    credentials += Credentials("Artifactory Realm", "ci-sb-1.obi.int", "publisher", "itouchb00ks"),
    publishTo <<= (version) { version: String =>
      val bksh = "http://ci-sb-1.obi.int:8081/artifactory/"
      if (version.trim.endsWith("SNAPSHOT")) Some("bookish" at bksh + "libs-snapshot-local/")
      else                                   Some("bookish" at bksh + "libs-release-local/")
    }  
  )

  lazy val BookishSettings = Project(
    id = "bookishSettings",
    base = file("."),
    settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.allDeps)
  )
}

object ShellPrompt {
  object devnull extends ProcessLogger {
    def info(s: => String) {}
    def error(s: => String) {}
    def buffer[T](f: => T): T = f
  }

  val current = """\*\s+([\w-]+)""".r
  def gitBranches = ("git branch --no-color" lines_! devnull mkString)
  val buildShellPrompt = {
    (state: State) =>
      {
        val currBranch = current findFirstMatchIn gitBranches map (_ group (1)) getOrElse "-"
        val currProject = Project.extract(state).currentProject.id
        "%s:%s> ".format(currProject, currBranch)
      }
  }
}

