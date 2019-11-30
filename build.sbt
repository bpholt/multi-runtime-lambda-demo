// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val fs2AwsV = "2.0.0-M5"

lazy val `multi-runtime-lambda-demo` =
  crossProject(JVMPlatform)
    .crossType(CrossType.Full)
    .in(file("."))
    .settings(
      organization := "com.planetholt",
      organizationHomepage := Option(url("https://www.planetholt.com")),
      description := "Demonstrates how to build a Scala app that can be deployed on multiple AWS Lambda runtimes (e.g. JVM, Node)",
      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
      resolvers ++= Seq(
        Resolver.jcenterRepo,
        Resolver.sonatypeRepo("releases"),
        Resolver.bintrayRepo("dwolla", "maven"),
      ),
      libraryDependencies ++= {
        val circeV = "0.12.2"
        Seq(
          "co.fs2" %%% "fs2-core" % "2.1.0",
          "io.circe" %%% "circe-literal" % circeV,
          "io.circe" %%% "circe-generic-extras" % circeV,
          "io.circe" %%% "circe-parser" % circeV,
          "io.circe" %%% "circe-generic-extras" % circeV,
          "io.circe" %%% "circe-optics" % "0.12.0",
          "com.chuusai" %%% "shapeless" % "2.3.3",
        ) ++
          Seq(
            "org.scalatest" %%% "scalatest" % "3.0.8",
            "com.dwolla" %%% "testutils-scalatest-fs2" % "2.0.0-M3",
          ).map(_ % Test)
      },
    )
    .jvmSettings(
      libraryDependencies ++= {
        Seq(
          "com.dwolla" %% "fs2-aws-java-sdk2" % fs2AwsV,
          "com.dwolla" %% "fs2-aws-lambda-io-app" % fs2AwsV,
          "software.amazon.awssdk" % "dynamodb" % "2.7.18",
        )
      }
    )

artifactPath := target.value / "awslambda.zip"
