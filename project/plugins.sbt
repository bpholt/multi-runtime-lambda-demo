addSbtPlugin("com.dwijnand" % "sbt-travisci" % "1.2.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.1")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.31")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.15.0-0.6")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.10")

resolvers += Resolver.bintrayRepo("oyvindberg", "ScalablyTyped")
addSbtPlugin("org.scalablytyped" % "sbt-scalablytyped" % "201911271221")
