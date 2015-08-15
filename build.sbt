lazy val root = (project in file(".")).
  settings(
    name := "CPU",
    version := "0.1",
    scalaVersion := "2.11.7",
    libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  )