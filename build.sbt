scalaVersion := "2.11.1"

name := "scala-async-join"

version := "0.0.1-SNAPSHOT"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided"

libraryDependencies += "com.netflix.rxjava" % "rxjava-scala" % "0.20.2"

libraryDependencies += "com.netflix.rxjava" % "rxjava-joins" % "0.20.4"

libraryDependencies += "com.novocode" % "junit-interface" % "0.10" % "test"

testOptions += Tests.Argument(TestFrameworks.JUnit, "+q", "-v", "-s")

libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.7-SNAPSHOT"

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

parallelExecution in Test := false

scalacOptions in compile ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint", "-feature")

scalacOptions in Test ++= Seq("-Yrangepos", "-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls")

startYear := Some(2014)
