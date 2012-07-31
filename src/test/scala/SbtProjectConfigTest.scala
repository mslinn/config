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

import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpec}

/**
  * @author Mike Slinn
  */
class SbtProjectConfigTest extends MustMatchers with WordSpec with BeforeAndAfterAll {

  override def beforeAll {
    SbtProjectConfig.quiet = false
    SbtProjectConfig.outerSectionName = "definitions"
    // assumes that the unit test is running from the project root
    SbtProjectConfig.fetchFromUrl = "file://" + sys.props.get("user.dir").get + "/target/classes/definitions.conf"
    println(SbtProjectConfig.fetchFromUrl)
  }

  "An SbtProjectConfig config" must {
    "read raw config file from github" in {

    }

    "read config file from jar" in {

    }

    "read config file from local file" in {

    }
  }

  "An SbtProjectConfig" must {
    "create V entries" in {
      expect("2.9.1", "")(V("scala"))

      V("akka")
      println(SbtProjectConfig.referencedRepos)
      assert(SbtProjectConfig.referencedRepos.contains("Typesafe Releases"))
      assert(!SbtProjectConfig.referencedRepos.contains("Typesafe Snapshots"))
      assert(!SbtProjectConfig.referencedRepos.contains("Bookish Releases"))
      assert(!SbtProjectConfig.referencedRepos.contains("Bookish Snapshots"))

      V("bkshDomainBus")
      println(SbtProjectConfig.referencedRepos)
      assert(SbtProjectConfig.referencedRepos.contains("Typesafe Releases"))
      assert(!SbtProjectConfig.referencedRepos.contains("Typesafe Snapshots"))
      assert(!SbtProjectConfig.referencedRepos.contains("Bookish Releases"))
      assert(SbtProjectConfig.referencedRepos.contains("Bookish Snapshots"))
    }

    "create server entries" in {
      expect("http://ci-sb-1.obi.int:8081/artifactory/", "")(servers("artifactory"))
    }

    "create creds entries" in {
      expect("itouchb00ks", "")(creds("password"))
    }

    "create repositories entries" in {
      expect("http://repo.typesafe.com/typesafe/releases/", "")(repositories("Typesafe Releases"))
    }

    "create vToRepo entries" in {
      expect("http://repo.typesafe.com/typesafe/releases/", "")(vToRepo.maybeAddRepo("akka", "2.1.0"))
      expect("http://repo.typesafe.com/typesafe/releases/", "") (SbtProjectConfig.repoUrl("Typesafe Releases"))

      expect("http://repo.typesafe.com/typesafe/snapshots/", "")(vToRepo.maybeAddRepo("akka", "2.1.0-SNAPSHOT"))
      assert(SbtProjectConfig.referencedRepos.contains("Typesafe Snapshots"))

      expect("", "")(vToRepo.maybeAddRepo("dbpool", "")) // special case; no repo
      expect("", "")(vToRepo.maybeAddRepo("blah", "1.2.3"))
    }

    "gracefully reject undefined entries" in {
      expect("", "")(V("blah"))
    }
  }
}
