def commonSettings = Seq(
  scalaVersion := "2.11.1",
  resolvers += Resolver.sonatypeRepo("snapshots"),
  resolvers += Resolver.sonatypeRepo("releases"),
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
  libraryDependencies += "io.reactivex" % "rxscala_2.11" % "0.21.1",
  libraryDependencies += "io.reactivex" % "rxjava-joins" % "0.21.0",
  libraryDependencies += "com.novocode" % "junit-interface" % "0.10" % "test",
  libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.7-SNAPSHOT",
  testOptions += Tests.Argument(TestFrameworks.JUnit, "+q", "-v", "-s"),
  testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
  parallelExecution in Test := false,
  scalacOptions in compile ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint", "-feature"),
  scalacOptions in Test ++= Seq("-Yrangepos", "-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls")
)

lazy val core: Project = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "scala-async-join",
    version := "0.0.1-SNAPSHOT",
    startYear := Some(2014)
  )

lazy val benchmark: Project = (project in file("benchmark")).
  dependsOn(core).
  settings(commonSettings: _*)
