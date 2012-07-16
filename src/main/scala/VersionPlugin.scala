package com.bookish.config

import com.typesafe.config.{ Config, ConfigFactory }
import java.net.URL
import sbt._
import Keys._
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
  val sampleUrl = "https://raw.github.com/Bookish/config/master/scalaBuild/Build.conf"

  // fixme how to set to sampleUrl if not user does not specify a value?
  val configUrl = SettingKey[String]("config-url")
  val configConfig = TaskKey[Config]("config-config")
  val configValues = TaskKey[Values]("config-values")

  override def settings = Seq(
    configUrl := sampleUrl,
    configConfig <<= configUrl map (url => ConfigFactory.parseURL(new URL(url))),
    configValues <<= configConfig map (new Values(_)),
    libraryDependencies <<= (configValues, libraryDependencies) apply ( (values, deps) => {
      // check deps for moduleIDs with group and artifact defined in values.sbtDependencies 
      // and set the configured version
      // this needs a different layout of the config file
      deps
    })
  )

  // this is probably not the way to expose the looked up values
  case class Values(config: Config) {
    val sbtDependencies = new Lookup(config, "versions", "versions of dependencies")
    val credentials     = new Lookup(config, "credentials")
    val servers         = new Lookup(config, "servers", "servers")
  }

  class Lookup(config: Config, section: String, label: String="") {
    val alreadyShown = mutable.HashSet.empty[String]

    def read(key: String) = {
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
