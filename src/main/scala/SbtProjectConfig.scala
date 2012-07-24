package com.bookish.config

import SbtProjectConfig._
import com.typesafe.config.{Config, ConfigFactory}
import java.net.URL
import scala.collection.mutable
import collection.JavaConversions._
import collection.immutable
import java.util

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

  val referencedRepos = mutable.HashSet.empty[String]

  def repoUrl(repoName: String): String = repositories.keyValues.getOrElse(repoName, "").toString
}

class SbtProjectConfig {
  private[config] val alreadyShown = mutable.HashSet.empty[String]
  private[config] val keyValues = mutable.HashMap.empty[String, Any]
  private[config] val entireConfig: Config = ConfigFactory.parseURL(new URL(fetchFromUrl))
//  println("Config fetched from %s:\n%s".format(url, entireConfig.toString))

  def apply(key: String): String = get(key).toString

  def get(key: String): Any = {
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

  def apply(key: String, version: String): String = {
    val value = get(key)
    val repo: String = if (version.endsWith("SNAPSHOT")) {
      if (value.isInstanceOf[util.ArrayList[String]] && value.asInstanceOf[util.ArrayList[String]].length>1)
        value.asInstanceOf[util.ArrayList[String]](1)
      else
        "problemFound"
    } else {
      if (value.isInstanceOf[util.ArrayList[String]] && value.asInstanceOf[util.ArrayList[String]].length>1)
        value.asInstanceOf[util.ArrayList[String]](0)
      else
        value.toString
    }
    //println(repositories.keyValues)
    val repoUrl: String = repositories.keyValues.getOrElse(repo, "").toString
    if (repoUrl.length>0) {
      referencedRepos += repo
      println("Adding %s to referencedRepos".format(repo))
    }
    repoUrl
  }

  /** Work around Config peculiarity */
  def sanitizeKey(key: String) = key.replace("\"", " ").trim

  /** Only invoked by vToRepo */
  def makeSettings(lookup: Lookup): immutable.Map[String, Any] = {
    lookup.config.entrySet foreach { kv =>
      val key = kv.getKey
      val value = kv.getValue.unwrapped
      keyValues.put(sanitizeKey(key), value)
    }
    keyValues.toMap[String, Any]
  }

  class Lookup(val section: String, label: String="") {
    if (!quiet)
      println("Defining " + section)
    val config = entireConfig.getConfig("%s.%s".format(outerSectionName, section))

    def apply(key: String) = config.getString("%s".format(key))
  }
}
