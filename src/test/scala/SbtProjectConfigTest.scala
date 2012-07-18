package com.bookish.config

import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpec}

/**
  * @author Mike Slinn
  */
class SbtProjectConfigTest extends MustMatchers with WordSpec with BeforeAndAfterAll {

  override def beforeAll {
  }

  "An SbtProjectConfig config" must {
    "Read raw config file from github" in {

    }

    "Read config file from jar" in {

    }

    "Read config file from local file" in {

    }
  }

  "An SbtProjectConfig" must {
    "Create V entries" in {
      expect("2.9.1", "")(V("scala"))
    }

    "Create server entries" in {
      expect("http://ci-sb-1.obi.int:8081/artifactory/", "")(servers("artifactory"))
    }

    "Create creds entries" in {
       expect("itouchb00ks", "")(creds("password"))
    }

    "Gracefully reject undefined entries" in {
      expect("", "")(V("blah"))
    }
  }
}
