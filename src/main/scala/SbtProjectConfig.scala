package com.bookish.config

import SbtProjectConfig._
import com.typesafe.config.{Config, ConfigFactory}
import java.net.URL
import scala.collection.mutable
import collection.JavaConversions._

object V extends SbtProjectConfig {
  val spc = new SbtProjectConfig

  val lookup = new Lookup("versions", "versions of dependencies")
  makeSettings("V", lookup)
}

object creds extends SbtProjectConfig {
  val lookup = new Lookup("credentials")
  makeSettings("creds", lookup)
}

object servers extends SbtProjectConfig {
  val lookup = new Lookup("servers", "servers")
  makeSettings("servers", lookup)
}

object SbtProjectConfig {
  var outerSectionName: String = "definitions"
  //val sampleUrl = "https://raw.github.com/Bookish/config/master/src/scala/main/resource/definitions.conf" // uncomment after v2 branch is merged to master
  val sampleUrl = "https://raw.github.com/Bookish/config/master/scalaBuild/Build.conf" // delete after v2 branch is merged to master
  val jarUrl    = "file:///home/mslinn/.ivy2/local/com.bookish/config/scala_2.9.1/sbt_0.11.3/0.1.0-SNAPSHOT/jars/config.jar!/definitions.conf"
}

class SbtProjectConfig(fetchFromUrl: String = sampleUrl) {
  private val keyValues = mutable.HashMap.empty[String, String]

  private var entireConfig: Config = ConfigFactory.parseURL(new URL(fetchFromUrl))
//  println("Config fetched from %s:\n%s".format(url, entireConfig.toString))

  def apply(key: String) = {
    if (keyValues.contains(key)) {
      keyValues.get(key).get
    } else {
      println("Warning: %s is not defined in the config file at %s".format(key, fetchFromUrl))
      ""
    }
  }

  def makeSettings(prefix:String, lookup: Lookup): Unit = {
    lookup.config.entrySet foreach { kv =>
      val key = kv.getKey
      val value = kv.getValue.unwrapped.toString
      println("  %s = %s".format(key, value))
      keyValues.put(key, value)
    }
  }

  class Lookup(val section: String, label: String="") {
    val alreadyShown = mutable.HashSet.empty[String]
    println("Defining " + section)
    val config = entireConfig.getConfig("%s.%s".format(outerSectionName, section))

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
