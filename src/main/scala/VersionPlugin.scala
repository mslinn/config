import sbt._
import com.typesafe.config.ConfigFactory
import java.net.URL


object VersionPlugin extends Plugin {

}

private class Lookup(section: String, label: String="") {
  val configUrl = new URL("https://raw.github.com/Bookish/config/master/scalaBuild/Build.conf")
  val parsedConfig = ConfigFactory.parseURL(configUrl)
  if (label.length>0) // credentials are not displayed
    println("Using the following %s:".format(label))

  def lookup(key: String) = {
    val value: String = parsedConfig.getString("bookishDeps.%s.%s".format(section, key))
    if (label.length>0) // credentials are not displayed
      println("  " + key + "=" + value)
    value
  }
}

object V {
  private val versions = new Lookup("versions", "versions of dependencies")

  val scalaVersion     = versions.lookup("scala")

  val bkshBatch        = versions.lookup("bkshBatch")
  val bkshCommon       = versions.lookup("bkshCommon")
  val bkshData         = versions.lookup("bkshData")
  val bkshDomainBus    = versions.lookup("bkshDomainBus")
  val bkshIngest       = versions.lookup("bkshIngest")
  val bkshPredictor    = versions.lookup("bkshPredictor")
  val bkshService      = versions.lookup("bkshService")
  val bkshSolr         = versions.lookup("bkshSolr")

  val Akka             = versions.lookup("akka")
  val amazonAWS        = versions.lookup("amazonAWS")
  val antiXml          = versions.lookup("antiXml")
  val argot            = versions.lookup("argot")
  val camel            = versions.lookup("camel")
  val casbah           = versions.lookup("casbah")
  val commonsLogging   = versions.lookup("commonsLogging")
  val commonsNet       = versions.lookup("commonsNet")
  val dbpool           = versions.lookup("dbpool")
  val imageScaling     = versions.lookup("imageScaling")
  val jerkson          = versions.lookup("jerkson")
  val jetty            = versions.lookup("jetty")
  val jettyClient      = versions.lookup("jettyClient")
  val jsoup            = versions.lookup("jsoup")
  val junit            = versions.lookup("junit")
  val logback          = versions.lookup("logback")
  val postgres         = versions.lookup("postgres")
  val protobuf         = versions.lookup("protobuf")
  val salat            = versions.lookup("salat")
  val scalaStm         = versions.lookup("scalaStm")
  val scalatest        = versions.lookup("scalatest")
  val slf4j            = versions.lookup("slf4j")
  val solr             = versions.lookup("solr")
  val subset           = versions.lookup("subset")
  val zmqScalaBinding  = versions.lookup("zmqScalaBinding")
}

object creds {
  private val credentials = new Lookup("credentials")

  val userid   = credentials.lookup("userid")
  val password = credentials.lookup("password")
}

object servers {
  private val servers = new Lookup("servers", "servers")

  val artifactory = servers.lookup("artifactory")
}
