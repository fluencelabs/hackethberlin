resolvers += Resolver.sonatypeRepo("releases")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.4.0")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)