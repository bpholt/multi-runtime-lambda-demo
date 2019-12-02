// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val fs2AwsV = "2.0.0-M5"

lazy val `multi-runtime-lambda-demo` =
  crossProject(JSPlatform, JVMPlatform)
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
    .jsSettings(
      libraryDependencies ++= {
        Seq(
          ScalablyTyped.A.`aws-sdk__client-dynamodb-node`,
        )
      },
      (npmDependencies in Compile) ++= Seq(
        "aws-xray-sdk-core" -> "1.2.0",
      ),
//      (npmDevDependencies in Compile) ++= Seq(
//        "serverless" -> "^1.26.1",
//        "serverless-plugin-tracing" -> "^2.0.0",
//      ),
      jsDependencies ++= Seq(
        "org.webjars.npm" % "aws-sdk" % "2.200.0" / "aws-sdk.js" minified "aws-sdk.min.js" commonJSName "AWS",
      ).map(_ % Test),
      webpackConfigFile := Some(baseDirectory.value / "webpack-config.js"),
      webpackResources := webpackResources.value +++ PathFinder(baseDirectory.value / "serverless.yml"),
      scalaJSModuleKind := ModuleKind.CommonJSModule,
      scalacOptions += "-P:scalajs:sjsDefinedByDefault",
      scalacOptions --= Seq(
        "-Wdead-code",
        "-Wunused:params",
      )
    )
    .enablePlugins(ScalaJSBundlerPlugin)

lazy val serverlessDeployCommand = settingKey[String]("serverless command to deploy the application")
serverlessDeployCommand := "serverless deploy --verbose"

lazy val deploy = taskKey[Int]("deploy to AWS")
deploy := Def.task {
  (Compile / Keys.`package`).value

  import scala.sys.process._

  val cmd = serverlessDeployCommand.value
  val webpackWorkingFolder = (Compile / npmUpdate / crossTarget).value
  val nodeModulesBin = webpackWorkingFolder / "node_modules" / ".bin"
  val bundleName = applicationBundles.value.map(_.name.split('.').head).head

  Process(
    s"$nodeModulesBin/$cmd",
    Option(webpackWorkingFolder),
    "SERVICE_NAME" -> normalizedName.value,
    "ARTIFACT_PATH" -> artifactPath.value.toString,
    "BUNDLE_NAME" -> bundleName,
  ).!
}.value

artifactPath := target.value / "awslambda.zip"

Keys.`package` in Compile := {
  val zipFile = artifactPath.value
  val inputs = applicationBundles.value.map(f => f -> f.name)

  IO.zip(inputs, zipFile)

  zipFile
}

lazy val applicationBundles = taskKey[Seq[File]]("webpack bundled application library")
applicationBundles := (`multi-runtime-lambda-demo`.js / Compile / fullOptJS / webpack).value
  .filter(_.metadata.get(BundlerFileTypeAttr).exists(_ == BundlerFileType.ApplicationBundle))
  .map(_.data)
