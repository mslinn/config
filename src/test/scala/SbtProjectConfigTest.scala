package com.bookish.config

import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpec}

/**
  * @author Mike Slinn
  */
class SbtProjectConfigTest extends MustMatchers with WordSpec with BeforeAndAfterAll {

  override def beforeAll {
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
      expect("http://repo.typesafe.com/typesafe/releases/", "")(vToRepo("akka", "2.1.0"))
      assert(SbtProjectConfig.referencedRepos.contains("Typesafe Releases"))
      assert(!SbtProjectConfig.referencedRepos.contains("Typesafe Snapshots"))
      expect("http://repo.typesafe.com/typesafe/releases/", "") (SbtProjectConfig.repoUrl("Typesafe Releases"))

      expect("http://repo.typesafe.com/typesafe/snapshots/", "")(vToRepo("akka", "2.1.0-SNAPSHOT"))
      assert(SbtProjectConfig.referencedRepos.contains("Typesafe Snapshots"))

      expect("", "")(vToRepo("dbpool", "")) // special case; no repo
      expect("", "")(vToRepo("blah", "1.2.3"))
    }

    "gracefully reject undefined entries" in {
      expect("", "")(V("blah"))
    }
  }
}
