import aether._
import sbt._
import sbt.Keys._
import xml.Group

object Build extends sbt.Build {

  lazy val buildSettings = Defaults.defaultSettings ++ Aether.aetherPublishSettings ++ Seq(
    organization := "net.hamnaberg",
    scalaVersion := "2.10.1",
    crossScalaVersions := Seq("2.10.1", "2.9.3", "2.9.2", "2.9.1"),
    scalacOptions <<= (scalaVersion) map {(sv: String) => 
      val twoTen = if (sv.startsWith("2.10")) List("-language:implicitConversions") else Nil 
      "-deprecation" :: twoTen      
    },
    publishTo <<= (version) apply {
      (v: String) => if (v.trim().endsWith("SNAPSHOT")) Some(Resolvers.sonatypeNexusSnapshots) else Some(Resolvers.sonatypeNexusStaging)
    },
    pomIncludeRepository := { x => false },
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials")      
  )

  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = buildSettings ++ Seq(
      description := "Sample Deploy project",
      name := "sbt-multi-deploy", 
      libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"      
    ) ++ mavenCentralFrouFrou
  )

  object Resolvers {
    val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    val sonatypeNexusStaging = "Sonatype Nexus Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
  }

  // Things we care about primarily because Maven Central demands them
  lazy val mavenCentralFrouFrou = Seq(
    homepage := Some(new URL("http://github.com/hamnis/sbt-multi-deploy")),
    startYear := Some(2013),
    licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))),
    pomExtra <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ Group(
      <scm>
        <url>https://github.com/hamnis/sbt-multi-deploy</url>
        <connection>scm:git:git://github.com/hamnis/sbt-multi-deploy.git</connection>
        <developerConnection>scm:git:git@github.com:hamnis/sbt-multi-deploy.git</developerConnection>
      </scm>
      <developers>
        <developer>
          <id>hamnis</id>
          <name>Erlend Hamnaberg</name>
          <url>http://twitter.com/hamnis</url>
        </developer>
      </developers>
    )}
  )
}
