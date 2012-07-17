package com.bookish.config

import com.typesafe.config.{ConfigValue, Config, ConfigFactory}
import java.net.URL
import sbt._
import Keys._
import scala.collection.mutable
import collection.JavaConversions._

object SbtDependencies extends Plugin {
  val configUrl            = SettingKey[String]("config-url", "URL to fetch config file from")

  //val sampleUrl = "https://raw.github.com/Bookish/config/master/src/scala/main/resource/definitions.conf" // uncomment after v2 branch is merged to master
  val sampleUrl = "https://raw.github.com/Bookish/config/master/scalaBuild/Build.conf" // delete after v2 branch is merged to master
  val jarUrl    = "file:///home/mslinn/.ivy2/local/com.bookish/config/scala_2.9.1/sbt_0.11.3/0.1.0-SNAPSHOT/jars/config.jar!/definitions.conf"

  val config = ConfigFactory.parseURL(new URL(sampleUrl)) // fixme only want to set to sampleUrl if not user does not specify a value.

  override def settings = Seq(
    configUrl := sampleUrl // fixme only want to set to sampleUrl if not user does not specify a value.
  ) ++
  makeSettings("V", new Lookup(config, "versions", "versions of dependencies")) ++
  makeSettings("creds", new Lookup(config, "credentials")) ++
  makeSettings("servers", new Lookup(config, "servers", "servers"))

  def makeSettings(prefix:String, lookup: Lookup): Seq[sbt.Project.Setting[_]] = {
    (lookup.config.entrySet map { kv =>
      val key = prefix + "." + kv.getKey
      val value = kv.getValue.unwrapped.toString
      val settingKey = SettingKey[String](key)
      println("  %s = %s".format(key, value))
      settingKey := value
    }).toSeq
  }

  class Lookup(entireConfig: Config, val section: String, label: String="") {
    val alreadyShown = mutable.HashSet.empty[String]
    println("Creating " + section)
    val config = entireConfig.getConfig("bookishDeps.%s".format(section))

    def apply(key: String) = {
      val value: String = config.getString("%s".format(key))
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
