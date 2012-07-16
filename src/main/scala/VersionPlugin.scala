package com.bookish.config

import com.typesafe.config.{ Config, ConfigFactory }
import java.net.URL
import sbt._
import scala.collection.mutable

object SbtDependencies extends Plugin {
  val fetchFromUrlKey = SettingKey[String]("fetch-from-url", "URL to fetch config file from")

  val versionsTask = TaskKey[Lookup]("V", "Dependency version numbers parsed from config file")
  versionsTask <<= fetchFromUrlKey map { (urlStr: String) => // never gets called
    println("versionsTask was called")
    fetchFromUrl = new URL(urlStr) // sets config as a side effect
    new Lookup(config, "versions", "versions of dependencies") }

  val credentialsTask = TaskKey[Lookup]("credentials", "Userids and passwords parsed from config file")
  credentialsTask <<= fetchFromUrlKey map { (urlStr: String) => // never gets called
    println("credentialsTask was called")
    fetchFromUrl = new URL(urlStr) // sets config as a side effect
    new Lookup(config, "credentials") }

  val serversTask = TaskKey[Lookup]("servers", "Server URLs parsed from config file")
  serversTask <<= fetchFromUrlKey map { (urlStr: String) => // never gets called
    println("serversTask was called")
    fetchFromUrl = new URL(urlStr) // sets config as a side effect
    new Lookup(config, "servers", "servers") }

  //val sampleUrl = new URL("https://raw.github.com/Bookish/config/master/src/scala/main/resource/definitions.conf") // uncomment after v2 branch is merged to master
  val sampleUrl = new URL("https://raw.github.com/Bookish/config/master/scalaBuild/Build.conf") // delete after v2 branch is merged to master
  val jarUrl    = new URL("file:///home/mslinn/.ivy2/local/com.bookish/config/scala_2.9.1/sbt_0.11.3/0.1.0-SNAPSHOT/jars/config.jar!/definitions.conf")

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
}

class Lookup(config: Config, section: String, label: String="") {
  val alreadyShown = mutable.HashSet.empty[String]
  println("Created Lookup " + section)

  def apply(key: String) = {
    val value: String = config.getString("bookishDeps.%s.%s".format(section, key))
    //val value: String = SbtDependencies.config.getString("definitions.%s.%s".format(section, key))
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
