package com.bookish.config

import com.typesafe.config.{ Config, ConfigFactory }
import java.net.URL
import sbt._
import scala.collection.mutable

object SbtDependencies extends Plugin {
  //val sampleUrl = new URL("https://raw.github.com/Bookish/config/master/src/scala/main/resource/definitions.conf")
  val sampleUrl = new URL("https://raw.github.com/Bookish/config/master/scalaBuild/Build.conf")
  val jarUrl    = new URL("file:///home/mslinn/.ivy2/local/com.bookish/config/scala_2.9.1/sbt_0.11.3/0.1.0-SNAPSHOT/jars/config.jar!/definitions.conf")

  private var _config: Config = ConfigFactory.empty
  private def config: Config = _config

  private var _url: URL = sampleUrl

  /** URL specified by user, defaults to value of `SbtDependencies.sampleUrl` */
  def fetchFromUrl = _url

  /** URL specified by user, defaults to value of `SbtDependencies.sampleUrl` */
  def fetchFromUrl_= (url: URL): Unit = {
    _url = url
    _config = ConfigFactory.parseURL(url)
  }

  class Lookup(section: String, label: String="") {
    val alreadyShown = mutable.HashSet.empty[String]

    def apply(key: String) = {
      val value: String = config.getString("definitions.%s.%s".format(section, key))
      if (!alreadyShown.contains(key)) { // only display each key a maximum of one time
          alreadyShown += key
          if (label.length>0) // credential values are not displayed
            println("  " + key + "=" + value)
          else
            println("  " + key + " was retrieved")
      }
      value
    }
  }

  lazy val V           = new Lookup("versions", "versions of dependencies")
  lazy val credentials = new Lookup("credentials")
  lazy val servers     = new Lookup("servers", "servers")
}
