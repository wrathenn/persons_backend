ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.0"

val versions = new {
  val cats = "3.5.0"
  val http4s = "0.23.23"
  val circe = "0.14.5"
  val fs2 = "3.7.0"
  val doobie = "1.0.0-RC4"
}

libraryDependencies += "org.typelevel" %% "cats-effect" % versions.cats

libraryDependencies ++= Seq(
  "http4s-dsl",
  "http4s-circe",
  "http4s-ember-client",
  "http4s-ember-server",
).map("org.http4s" %% _ % versions.http4s)

libraryDependencies ++= Seq(
  "circe-core",
  "circe-generic",
  "circe-parser",
).map("io.circe" %% _ % versions.circe)

libraryDependencies ++= Seq(
  "fs2-core",
  "fs2-io",
).map("co.fs2" %% _ % versions.fs2)

libraryDependencies ++= Seq(
  "doobie-core",
  "doobie-hikari",
  "doobie-postgres",
  "doobie-specs2",
).map("org.tpolecat" %% _ % versions.doobie)
