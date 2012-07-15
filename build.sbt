sbtPlugin           := true

name                := "config"

organization        := "com.bookish"

crossPaths          := false

version	            := "0.1.0-SNAPSHOT"

scalaVersion        := "2.9.1"

scalacOptions	    ++= Seq("-deprecation", "-unchecked")

libraryDependencies += "com.typesafe" % "config" % "0.5.0"

credentials         += Credentials("Artifactory Realm", "ci-sb-1.obi.int", "publisher", "itouchb00ks")

publishTo <<= (version) { version: String =>
  if (version.trim.endsWith("SNAPSHOT"))
    Some("bookish" at "http://ci-sb-1.obi.int:8081/artifactory/libs-snapshot-local/")
  else
    Some("bookish" at "http://ci-sb-1.obi.int:8081/artifactory/libs-release-local/")
}
