import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.scalamacros",
    version := "1.0.0-alpha",
    scalaVersion := "2.11.0",
    crossScalaVersions := Seq("2.11.0"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    // scalacOptions ++= Seq("")
    scalacOptions ++= Seq("-unchecked", "-deprecation")
//    scalacOptions ++= Seq("-Ymacro-debug-lite")
//    scalacOptions ++= Seq("-Ybrowse:typer")
  )
}

object MyBuild extends Build {
  import BuildSettings._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings ++ Seq(
      run <<= run in Compile in core)
  ) aggregate(macros, core)

  lazy val macros: Project = Project(
    "macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % "2.11.0",
        "com.netflix.rxjava" % "rxjava-scala" % "0.18.3"))
  )

  lazy val core: Project = Project(
    "core",
    file("core"),
    settings = buildSettings ++ Seq(
      libraryDependencies += ("com.netflix.rxjava" % "rxjava-scala" % "0.18.3"))
  ) dependsOn(macros)
}
