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

  override def settings = Seq(
    configUrl               := sampleUrl, // fixme only want to set to sampleUrl if not user does not specify a value.
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
    println("Created Lookup " + section) // never called

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
