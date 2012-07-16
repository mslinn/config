package com.bookish.config

import com.typesafe.config.{ Config, ConfigFactory }
import java.net.URL
import sbt._
import Keys._
import scala.collection.mutable

object SbtDependencies extends Plugin {
  val configUrl            = SettingKey[String]("config-url", "URL to fetch config file from")
  private val configConfig    = TaskKey[Config]("config-config")
  val configVersionsLookup    = TaskKey[Lookup]("config-versions", "Dependency version numbers parsed from config file")
  val configCredentialsLookup = TaskKey[Lookup]("config-credentials", "versions of dependencies")
  val configServersLookup     = TaskKey[Lookup]("config-servers", "Userids and passwords parsed from config file")

  //val sampleUrl = "https://raw.github.com/Bookish/config/master/src/scala/main/resource/definitions.conf" // uncomment after v2 branch is merged to master
  val sampleUrl = "https://raw.github.com/Bookish/config/master/scalaBuild/Build.conf" // delete after v2 branch is merged to master
  val jarUrl    = "file:///home/mslinn/.ivy2/local/com.bookish/config/scala_2.9.1/sbt_0.11.3/0.1.0-SNAPSHOT/jars/config.jar!/definitions.conf"

  /** URL specified by user, defaults to value of `SbtDependencies.sampleUrl`.
   * If called with the same URL twice, only fetches and parses URL the first time */
  def fetchFromUrl_= (url: URL): Unit = {
    if (_url == url)
      return

    _url = url
    _config = ConfigFactory.parseURL(_url)
    println("SbtDependencies: fetched from %s:\n%s".format(url, _config.toString))
  }

  /** URL specified by user, defaults to value of `SbtDependencies.sampleUrl` */
  def fetchFromUrl = _url

  private var _config: Config = ConfigFactory.empty
  private[config] def config: Config = _config

  private var _url: URL = _url

  override def settings = Seq(
    configConfig            <<= configUrl    map { (urlStr: String) => ConfigFactory.parseURL(new URL(urlStr)) },
    configVersionsLookup    <<= configConfig map (new Lookup(_, "versions", "versions of dependencies")),
    configCredentialsLookup <<= configConfig map (new Lookup(_, "credentials")),
    configServersLookup     <<= configConfig map (new Lookup(_, "servers", "servers"))/*,
    libraryDependencies     <<= (configVersionsLookup, libraryDependencies) apply ( (values, deps) => {
      // check deps for moduleIDs with group and artifact defined in values.sbtDependencies
      // and set the configured version
      // this needs a different layout of the config file
      deps
    }*/
  )

  case class Lookup(config: Config, section: String, label: String="") {
    val alreadyShown = mutable.HashSet.empty[String]
    println("Created Lookup " + section)

    def read(key: String) = {
      val value: String = config.getString("bookishDeps.%s.%s".format(section, key))
      //val value: String = config.getString("definitions.%s.%s".format(section, key))
      if (!alreadyShown.contains(key)) { // only display each key a maximum of one time
          alreadyShown += key
          if (label.length>0) // credential values are not displayed
            println("  " + section + "." + key + "=" + value)
          else
            println("  " + section + "." + key + " was retrieved")
      }
      value
    }
  }
}
