import Dependencies._

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.bxroberts",
      scalaVersion := "2.12.8",
      version      := "0.0.2"
    )),
    name := "PDFTableVulture",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      "com.github.tototoshi" %% "scala-csv" % "1.3.5",
    ),
    scalacOptions += "-feature",
    scalacOptions += "-Yresolve-term-conflict:object"
  )
