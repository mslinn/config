name                := "config"

organization        := "com.bookish"

crossPaths          := false

version	            := "0.3.1-SNAPSHOT"

scalaVersion        := "2.9.1"

scalacOptions	    ++= Seq("-deprecation", "-unchecked")

libraryDependencies += "com.typesafe"  %  "config"   % "0.5.0" withSources()

//libraryDependencies += "org.scala-sbt" %%  "sbt"     % "0.11.3" withSources()

libraryDependencies += "org.scalatest" %% "scalatest" % "1.7.1" % "test" withSources()

credentials         += Credentials("Artifactory Realm", "ci-sb-1.obi.int", "publisher", "itouchb00ks")

publishArtifact in Test := false

publishTo <<= (version) { version: String =>
val artifactory = "http://ci-sb-1.obi.int:8081/artifactory/"
  if (version.trim.endsWith("SNAPSHOT"))
    Some("bookish" at artifactory + "libs-snapshot-local/")
  else
    Some("bookish" at artifactory + "libs-release-local/")
}

//artifactName := { (config: String, module: ModuleID, artifact: Artifact) =>
//  "asdf-" + artifact.name + "-" + module.revision + "." + artifact.extension
//}

//artifactPath := {
//  (crossTarget, projectID, art, scalaVersion, artifactName) =>
//    crossTarget / toString(scalaVersion, "qwer" + projectID, art) asFile
//}
