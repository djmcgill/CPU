val jmeVersion = "3.0.0.20140325-SNAPSHOT"

lazy val root = (project in file(".")).
  settings(
    name := "CPU",
    version := "0.1",
    scalaVersion := "2.11.7",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.4",
		libraryDependencies += "com.jme3" % "jmonkeyengine3" % jmeVersion,
		libraryDependencies += "com.jme3" % "jME3-desktop" % jmeVersion,
		libraryDependencies += "com.jme3" % "jME3-core" % jmeVersion,
		libraryDependencies += "com.jme3" % "jME3-lwjgl" % jmeVersion,
		libraryDependencies += "com.jme3" % "lwjgl" % jmeVersion,
		libraryDependencies += "com.jme3" % "jME3-lwjgl-natives" % jmeVersion,
		libraryDependencies += "com.jme3" % "jME3-jbullet" % jmeVersion,
		libraryDependencies += "com.jme3" % "jbullet" % jmeVersion,
		libraryDependencies += "com.jme3" % "jME3-desktop" % jmeVersion,
		libraryDependencies += "com.jme3" % "jinput" % jmeVersion
  )
  .settings(lwjglSettings: _*)


