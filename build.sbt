name := "sol-dsl"

version := "0.1"

scalaVersion := "2.12.6"

version                   := "0.1"
fork in Test              := true
parallelExecution in Test := false
organizationName          := "Fluence Labs Limited"
organizationHomepage      := Some(new URL("https://fluence.one"))
startYear                 := Some(2018)
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
//headerLicense := Some(License.ALv2("2018", organizationName.value))
resolvers += Resolver.bintrayRepo("fluencelabs", "releases")
scalafmtOnCompile := true
// see good explanation https://gist.github.com/djspiewak/7a81a395c461fd3a09a6941d4cd040f2
scalacOptions += "-Ypartial-unification"

lazy val macro_impl = project.in(file("macro_impl")).settings(
  libraryDependencies ++= Seq(
    "org.typelevel"       %% "cats-core"   % "1.2.0",
    "com.chuusai" %% "shapeless" % "2.3.3",
    scalaVersion("org.scala-lang" % "scala-reflect" % _).value,
    "org.scalatest" %% "scalatest"   % "3.0.5"  % Test
  )
)

lazy val root = project.in(file(".")).aggregate(macro_impl).dependsOn(macro_impl)

libraryDependencies ++= Seq(
  "org.typelevel"       %% "cats-core"   % "1.2.0",
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.scalatest" %% "scalatest"   % "3.0.5"  % Test
)