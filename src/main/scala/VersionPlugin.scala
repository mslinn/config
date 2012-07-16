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
  val configVersionsLookup = TaskKey[Lookup]("config-versions-lookup")
  val configCredentialsLookup = TaskKey[Lookup]("config-credentials-lookup")
  val configServersLookup = TaskKey[Lookup]("config-servers-lookup")

  override def settings = Seq(
    configUrl := sampleUrl,
    configConfig <<= configUrl map (url => ConfigFactory.parseURL(new URL(url))),
    configVersionsLookup <<= configConfig map (new Lookup(_, "versions", "versions of dependencies")),
    configCredentialsLookup <<= configConfig map (new Lookup(_, "credentials")),
    configServersLookup <<= configConfig map (new Lookup(_, "servers", "servers")),
    libraryDependencies <<= (configVersionsLookup, libraryDependencies) apply ( (values, deps) => {
      // check deps for moduleIDs with group and artifact defined in values.sbtDependencies 
      // and set the configured version
      // this needs a different layout of the config file
      deps
    })
  )

  case class Lookup(config: Config, section: String, label: String="") {
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
