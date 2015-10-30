name := "reactive-salesforce-rest-angular-crud"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  javaWs,
  cache,
  filters,
  "org.webjars" % "bootstrap" % "3.2.0",
  "org.webjars" % "angularjs" % "1.2.20",
  "org.webjars" % "ng-grid" % "2.0.11-2"
)


fork in run := true
