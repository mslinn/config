`SbtProjectConfig` is an SBT enhancement which allows you to manage the versions of your dependencies across multiple
projects in a consistent manner. If you have multiple SBT projects that need to come together, and you are tired of
discovering too late that they were built with differing versions of dependencies, then this SBT enhancment is for you.

`SbtProjectConfig` pulls dependency information from a file pointed to by a URI and provides variables with config
information to the host project. The file pointed to by the URI must be in
[HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md) format, and must specify the versions of every
direct dependency used in your projects.
Transitive dependencies cannot be controlled, so they are not listed.
If you want to examine transitive dependencies, see the [dependencyReport](https://github.com/mslinn/dependencyReport)
and [sbt-dependency-graph](https://github.com/jrudolph/sbt-dependency-graph) SBT plug-ins.

The URI can point to a file on your local machine or a file provided by a web server.
URIs must comply with the [java.net.URI](http://docs.oracle.com/javase/7/docs/api/java/net/URI.html) specification.
When building and testing each project locally, use local config files (`file://`) for testing modifications to
versioning. Once the modifications to your HOCON file work to your satisfaction, publish that file to its public
location for usage by all of your organization's projects.

`SbtProjectConfig` has a property called `fetchFromUrl`, and it should be set to the URI of the HOCON file.
Examples of the URI would include the a file on a local drive or an Internet address.
Here are some examples of valid `fetchFromUrl` values:

````
https://raw.github.com/mslinn/config/v3/src/main/resources/definitions.conf
file:///E:/work/config/test.conf
````

[An example of a compatible HOCON-format file](https://raw.github.com/mslinn/config/v2/src/main/resources/definitions.conf)
is provided with this project.

## Installation

 1. Get and install SBT from
````
https://github.com/harrah/xsbt
````

 1. Build and publish `SbtProjectConfig`:
````
git clone git@github.com/Bookish/config.git
cd config
sbt publish-local
````

 1. Add this to your project's `project/build.sbt` (remember that file requires double-spacing).
Note that this SBT enhancement is not a plug-in.
````
libraryDependencies += "com.bookish" % "config" % "0.3.1-SNAPSHOT" withSources()
````

## Sample Usage

See the [ConfigTest](https://github.com/mslinn/configTest) project for a small but complete working example of how to
use `SbtProjectConfig`.

In the following sample `build.scala`, note that `V()` defines version numbers of dependencies, `creds()` defines
userid and password, and `servers()` defines URLs of remote resources.

````
import com.bookish.config.{SbtProjectConfig, V, creds, servers}
import sbt._
import Keys._

// Point to the appropriate configuration file
SbtProjectConfig.fetchFromUrl = "https://raw.github.com/Bookish/config/v3/src/main/resources/definitions.conf"
SbtProjectConfig.quiet        = true

// Use configured versions for dependencies
val akkaActor = "com.typesafe.akka" %  "akka-actor"      % V("Akka")    withSources()
val junit     = "junit"             %  "junit"           % V("junit")   % "test"
val logback   = "ch.qos.logback"    %  "logback-classic" % V("logback") withSources()

// Use configured credentials authentication
credentials += Credentials("Artifactory Realm", "mysrvr", creds("userid"), creds("password"))

// Access configured servers
publishTo <<= (version) { version: String =>
  if (version.trim.endsWith("SNAPSHOT"))
    Some("bookish" at servers("artifactory") + "libs-snapshot-local/")
  else
    Some("bookish" at servers("artifactory") + "libs-release-local/")
}
````
