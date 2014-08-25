scalaVersion := "2.11.1"

name := "scala-async-join"

version := "0.0.1-SNAPSHOT"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided"

libraryDependencies += "com.netflix.rxjava" % "rxjava-scala" % "0.19.6"

libraryDependencies += "com.novocode" % "junit-interface" % "0.10" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

testOptions += Tests.Argument(TestFrameworks.JUnit, "+q", "-v", "-s")

scalacOptions in compile ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint", "-feature")

scalacOptions in Test ++= Seq("-Yrangepos", "-deprecation", "-feature", "-unchecked")

startYear := Some(2014)
