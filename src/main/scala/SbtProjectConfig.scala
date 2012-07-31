/*
 * Copyright 2012 Bookish, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. */

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

  private[config] val repositoriesDefined = {
    val hasRepositories = entireConfig.hasPath("%s.repositories".format(outerSectionName))
    val hasVToRepo = entireConfig.hasPath("%s.vToRepo".format(outerSectionName))
    if (hasRepositories && !hasVToRepo) {
      println("Warning: %s contains a '%s.repositories' section, but not a '%s.vToRepo' section".
        format(fetchFromUrl, outerSectionName, outerSectionName))
    } else if (!hasRepositories && hasVToRepo) {
      println("Warning: %s contains a '%s.vToRepo' section, but not a '%s.repositories' section".
        format(fetchFromUrl, outerSectionName, outerSectionName))
    } else if (hasRepositories && hasVToRepo) {
      if (!quiet)
        println("Using repository information in %s".format(fetchFromUrl))
    } else {
      //if (!quiet)
        println("No repository information found in %s; ensure that resolvers are explicitly specified in the build file".format(fetchFromUrl))
    }
    hasRepositories && hasVToRepo
  }


  def apply(key: String): String = {
    val value = get(key).toString
    if (repositoriesDefined)
      vToRepo.maybeAddRepo(key, value)
    value
  }

  def get(key: String, issueWarning: Boolean = true): Any = {
    val sanitizedKey = sanitizeKey(key)
    val value = keyValues.get(sanitizedKey)
    if (value!=None) {
      if (!quiet && !alreadyShown.contains(sanitizedKey)) {
        alreadyShown += sanitizedKey
        println("  %s = %s".format(sanitizedKey, value.get))
      }
      value.get
    } else {
      if (issueWarning)
        println("Warning: %s is not defined in the config file at %s".format(sanitizedKey, fetchFromUrl))
      ""
    }
  }

  /** Just used by repositories */
  def maybeAddRepo(key: String, version: String): String = {
    val value = get(key, false)
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
      if (!referencedRepos.contains(repo)) {
        if (!quiet)
          println("""  Adding "%s" to referencedRepos""".format(repo))
        referencedRepos += repo
      }
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
