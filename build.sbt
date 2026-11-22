name := """tic-tac-toe"""
organization := "com.kriuchkov"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.18"

libraryDependencies += guice

libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.24.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.24.1"
)

libraryDependencies ++= Seq(
  ehcache
)   