This sbt plugin pulls dependency information from a file pointed to by a URI and provides variables with config
information to the host project. The file pointed to by the URI must be in
[HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md) format, and must specify the versions of every
direct dependency used in your projects.
Transitive dependencies cannot be controlled, so they are not listed.
If you want to examine transitive dependencies, see the [dependencyReport](https://github.com/mslinn/dependencyReport)
and [sbt-dependency-graph](https://github.com/jrudolph/sbt-dependency-graph) SBT plug-ins.

The URI can point to an file contained in this plug-in, a file on your local machine, or a file provided by a web
server; URIs must comply with the [java.net.URI](http://docs.oracle.com/javase/7/docs/api/java/net/URI.html)
specification.
When building and testing each project locally, use local config files (`file://`) for testing modifications to
versioning. Once the modifications to your HOCON file work to your satisfaction, publish that file to its public
location for usage by all of your organization's projects.

This plug-in has a property called `fetchFromUrl`, and it should be set to the URI of the HOCON file.
Examples of the URI would include the plug-in's internal version file, a file on a local drive, or an Internet address.
Here are some examples of valid `fetchFromUrl` values:

````
https://raw.github.com/Bookish/config/master/src/scala/main/resource/definitions.conf
file:///home/mslinn/.ivy2/local/com.bookish/config/0.1-SNAPSHOT/jars/config.jar!/definitions.conf
file:///E:/work/config/test.conf
````

## Installation

 1. To build this code, get and install SBT from
````
https://github.com/harrah/xsbt
````

 1. Build and publish this plugin:
````
git clone git@github.com/Bookish/config.git
cd config
sbt publish-local
````

 1. Add this to your project's `project/plugins.sbt` (remember that file requires double-spacing):
````
addSbtPlugin("com.bookish" % "config" % "0.1.0-SNAPSHOT")
````

## Usage

Add the following to your project's `build.sbt` or `build.scala`:

````
// add imports for the plug-in
import com.bookish.config.SbtDependencies
import SbtDependencies._

import sbt._
import Keys._

// Point to the configuration file
SbtDependencies.fetchFromUrl = new java.net.URL("https://raw.github.com/Bookish/config/master/scalaBuild/Build.conf")

// Use configured versions for dependencies
val akkaActor = "com.typesafe.akka" %  "akka-actor"      % V.Akka    withSources()
val junit     = "junit"             %  "junit"           % V.junit   % "test"
val logback   = "ch.qos.logback"    %  "logback-classic" % V.logback withSources()

// Use configured credentials authentication
credentials += Credentials("Artifactory Realm", "ci-sb-1.obi.int", creds.userid, creds.password)

// Access configured servers
publishTo <<= (version) { version: String =>
  if (version.trim.endsWith("SNAPSHOT"))
    Some("bookish" at servers.artifactory + "libs-snapshot-local/")
  else
    Some("bookish" at servers.artifactory + "libs-release-local/")
}
````
