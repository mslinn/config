name                := "config"

organization        := "com.bookish"

crossPaths          := false

version	            := "0.4.0-SNAPSHOT"

scalaVersion        := "2.9.1"

scalacOptions	    ++= Seq("-deprecation", "-unchecked")

libraryDependencies += "com.typesafe"  %  "config"   % "0.5.0" withSources()

libraryDependencies += "org.scalatest" %% "scalatest" % "1.7.1" % "test" withSources()

credentials         += Credentials("Artifactory Realm", "ci-sb-1.obi.int", "publisher", "itouchb00ks")

publishArtifact in Test := false

publishTo <<= (version) { version: String =>
   val scalasbt = "http://scalasbt.artifactoryonline.com/scalasbt/"
   val (name, url) = if (version.contains("-SNAPSHOT"))
                       ("sbt-plugin-snapshots", scalasbt+"sbt-plugin-snapshots")
                     else
                       ("sbt-plugin-releases", scalasbt+"sbt-plugin-releases")
   Some(Resolver.url(name, new URL(url))(Resolver.ivyStylePatterns))
}

publishMavenStyle := false
