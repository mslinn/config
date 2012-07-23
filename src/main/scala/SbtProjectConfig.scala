package com.bookish.config

import SbtProjectConfig._
import com.typesafe.config.{Config, ConfigFactory}
import java.net.URL
import scala.collection.mutable
import collection.JavaConversions._
import collection.immutable.SortedMap

object V extends SbtProjectConfig {
  val lookup = new Lookup("versions", "versions of dependencies")
  makeSettings(lookup)
}

object creds extends SbtProjectConfig {
  val lookup = new Lookup("credentials")
  makeSettings(lookup)
}

object repositories extends SbtProjectConfig {
  val lookup = new Lookup("repositories")
  makeSettings(lookup)
}

object vToRepo extends SbtProjectConfig {
  val lookup = new Lookup("vToRepo")
  makeSettings(lookup)
}

object servers extends SbtProjectConfig {
  val lookup = new Lookup("servers", "servers")
  makeSettings(lookup)
}

object SbtProjectConfig {
  var outerSectionName: String = "definitions"

  //val sampleUrl = "https://raw.github.com/Bookish/config/master/src/scala/main/resource/definitions.conf" // uncomment after v2 branch is merged to master
  val sampleUrl = "https://raw.github.com/Bookish/config/master/scalaBuild/Build.conf" // delete after v2 branch is merged to master
  val jarUrl    = "file:///home/mslinn/.ivy2/local/com.bookish/config/scala_2.9.1/sbt_0.11.3/0.1.0-SNAPSHOT/jars/config.jar!/definitions.conf"

  var fetchFromUrl: String = sampleUrl

  var quiet: Boolean = false
}

class SbtProjectConfig {
  private val keyValues = mutable.HashMap.empty[String, String]
  val alreadyShown = mutable.HashSet.empty[String]

  private val entireConfig: Config = ConfigFactory.parseURL(new URL(fetchFromUrl))
//  println("Config fetched from %s:\n%s".format(url, entireConfig.toString))

  def apply(key: String) = {
    val sanitizedKey = sanitizeKey(key)
    val value = keyValues.get(sanitizedKey)
    if (value!=None) {
      if (!quiet && !alreadyShown.contains(sanitizedKey)) {
        alreadyShown += sanitizedKey
        println("  %s = %s".format(sanitizedKey, value.get))
      }
      value.get
    } else {
      println("Warning: %s is not defined in the config file at %s".format(sanitizedKey, fetchFromUrl))
      ""
    }
  }

  /** Work around Config bug */
  def sanitizeKey(key: String) = key.replace("\"", " ").trim

  def makeSettings(lookup: Lookup): SortedMap[String, String] = {
    lookup.config.entrySet foreach { kv =>
      val key = kv.getKey
      val value = kv.getValue.unwrapped.toString
      keyValues.put(sanitizeKey(key), value)
    }
    SortedMap.empty[String, String] ++ (keyValues.toList.sortBy(_._1))
  }

  class Lookup(val section: String, label: String="") {
    if (!quiet)
      println("Defining " + section)
    val config = entireConfig.getConfig("%s.%s".format(outerSectionName, section))

    def apply(key: String) = config.getString("%s".format(key))
  }
}
