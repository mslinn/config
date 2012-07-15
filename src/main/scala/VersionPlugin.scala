package com.bookish.config

import com.typesafe.config.{ Config, ConfigFactory }
import java.net.URL
import sbt._
import scala.collection.mutable

/* This is a mess. The SBT docs are incomprehensible.
Here is what I want to do:
1) Allow the user to specify a value for fetchFromUrl
2) Use a default value of fetchFromUrl if not specified
3) Use the value of _config to return the values of three lookups:
   a) sbtDependencies (exposed as "V")
   b) credentials (exposed as "creds")
   c) servers
Maybe some portion of the code below might be useful.
*/
object SbtDependencies extends Plugin {
  // fixme how to set to sampleUrl if not user does not specify a value?
  val fetchFromUrlKey = SettingKey[String]("fetch-from-url")
  fetchFromUrlKey := fetchFromUrl

  sbtDependencies <<= values

  // this is probably not the way to expose the looked up values
  object values {
    val sbtDependencies = new Lookup("versions", "versions of dependencies")
    val credentials     = new Lookup("credentials")
    val servers         = new Lookup("servers", "servers")
  }

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
}
